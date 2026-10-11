package com.renaissancerentals.api.blog;

import com.renaissancerentals.api.blog.source.SourceIndexEntry;
import com.renaissancerentals.api.blog.source.SourceMedia;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

/** The blog's database copy and the sync's bookkeeping. Plain SQL: the writes are conditional on the source version. */
@Repository
@RequiredArgsConstructor
public class BlogStore {

    public static final String SYNC_NAME = "blog";

    private final NamedParameterJdbcTemplate jdbc;

    /**
     * Stores the post unless a newer version is already here. A post that was taken down comes back at the same version
     * (publishing again does not change a note's version), so a removed row accepts an equal one.
     */
    public boolean upsert(BlogPost post) {
        return jdbc.update(
                        """
                INSERT INTO blog_post (id, bucket, source_version, title, excerpt, cover_ref, category, body,
                                       published_at, updated_at, removed_at, synced_at)
                VALUES (:id, :bucket, :version, :title, :excerpt, :cover, :category, :body,
                        :publishedAt, :updatedAt, NULL, NOW())
                ON CONFLICT (id) DO UPDATE SET
                    bucket = EXCLUDED.bucket, source_version = EXCLUDED.source_version, title = EXCLUDED.title,
                    excerpt = EXCLUDED.excerpt, cover_ref = EXCLUDED.cover_ref, category = EXCLUDED.category,
                    body = EXCLUDED.body, published_at = EXCLUDED.published_at, updated_at = EXCLUDED.updated_at,
                    removed_at = NULL, synced_at = NOW()
                WHERE blog_post.source_version < EXCLUDED.source_version
                   OR (blog_post.removed_at IS NOT NULL AND blog_post.source_version <= EXCLUDED.source_version)
                """,
                        new MapSqlParameterSource()
                                .addValue("id", post.id())
                                .addValue("bucket", post.bucket())
                                .addValue("version", post.sourceVersion())
                                .addValue("title", post.title())
                                .addValue("excerpt", post.excerpt())
                                .addValue("cover", post.coverRef())
                                .addValue("category", post.category())
                                .addValue("body", post.body())
                                .addValue("publishedAt", ts(post.publishedAt()))
                                .addValue("updatedAt", ts(post.updatedAt())))
                > 0;
    }

    /** Takes a post down and drops its text. Ignored if a newer version is here. */
    public boolean remove(String id, int version) {
        return jdbc.update(
                        """
                UPDATE blog_post SET removed_at = NOW(), body = '', source_version = :version
                WHERE id = :id AND removed_at IS NULL AND source_version <= :version
                """,
                        Map.of("id", id, "version", version))
                > 0;
    }

    /** Takes a post down without touching the version it was held at (for a post that is no longer public). */
    public boolean takeDown(String id) {
        return jdbc.update(
                        "UPDATE blog_post SET removed_at = NOW(), body = '' WHERE id = :id AND removed_at IS NULL",
                        Map.of("id", id))
                > 0;
    }

    public Optional<BlogPost> find(String id) {
        return jdbc.query(SELECT + " WHERE id = :id AND removed_at IS NULL", Map.of("id", id), MAPPER).stream()
                .findFirst();
    }

    /** Newest first. {@code query} matches title, excerpt and text; {@code category} narrows. */
    public List<BlogPost> list(String query, String category, int limit, int offset) {
        var sql = new StringBuilder(SELECT + " WHERE removed_at IS NULL");
        var params = new MapSqlParameterSource().addValue("limit", limit).addValue("offset", offset);
        if (query != null && !query.isBlank()) {
            sql.append(" AND (title ILIKE :q OR excerpt ILIKE :q OR body ILIKE :q)");
            params.addValue("q", "%" + escapeLike(query.trim()) + "%");
        }
        if (category != null && !category.isBlank()) {
            sql.append(" AND LOWER(category) = LOWER(:category)");
            params.addValue("category", category.trim());
        }
        sql.append(" ORDER BY published_at DESC NULLS LAST, id LIMIT :limit OFFSET :offset");
        return jdbc.query(sql.toString(), params, MAPPER);
    }

    /** Which of these posts are here and live: what a link to another post is checked against. */
    public Set<String> liveAmong(Collection<String> ids) {
        if (ids.isEmpty()) return Set.of();
        return Set.copyOf(jdbc.queryForList(
                "SELECT id FROM blog_post WHERE removed_at IS NULL AND id IN (:ids)",
                Map.of("ids", ids),
                String.class));
    }

    /** Every live post and the version held: for the nightly check against what is public. */
    public List<SourceIndexEntry> liveIndex() {
        return jdbc.query(
                "SELECT bucket, id, source_version FROM blog_post WHERE removed_at IS NULL",
                (rs, i) -> new SourceIndexEntry(
                        rs.getString("bucket") + "/" + rs.getString("id"), rs.getInt("source_version")));
    }

    // ---- images and documents

    public void putMedia(String ref, SourceMedia media) {
        jdbc.update(
                """
                INSERT INTO blog_media (ref, content_type, content, fetched_at) VALUES (:ref, :type, :content, NOW())
                ON CONFLICT (ref) DO UPDATE SET content_type = EXCLUDED.content_type, content = EXCLUDED.content, fetched_at = NOW()
                """,
                Map.of("ref", ref, "type", media.contentType(), "content", media.content()));
    }

    public boolean hasMedia(String ref) {
        return Boolean.TRUE.equals(jdbc.queryForObject(
                "SELECT EXISTS (SELECT 1 FROM blog_media WHERE ref = :ref)", Map.of("ref", ref), Boolean.class));
    }

    public Optional<StoredMedia> media(String ref) {
        return jdbc
                .query(
                        "SELECT content_type, content, fetched_at FROM blog_media WHERE ref = :ref",
                        Map.of("ref", ref),
                        (rs, i) -> new StoredMedia(
                                rs.getString("content_type"),
                                rs.getBytes("content"),
                                rs.getObject("fetched_at", OffsetDateTime.class)))
                .stream()
                .findFirst();
    }

    // ---- the sync's bookkeeping

    public Optional<String> cursor() {
        return jdbc
                .queryForList(
                        "SELECT cursor_value FROM sync_state WHERE name = :name AND cursor_value IS NOT NULL",
                        Map.of("name", SYNC_NAME),
                        String.class)
                .stream()
                .findFirst();
    }

    public void saveCursor(String cursor) {
        jdbc.update(
                """
                INSERT INTO sync_state (name, cursor_value, last_success_at) VALUES (:name, :cursor, NOW())
                ON CONFLICT (name) DO UPDATE SET cursor_value = EXCLUDED.cursor_value, last_success_at = NOW()
                """,
                Map.of("name", SYNC_NAME, "cursor", cursor));
    }

    /** A run that finished without a failure, even if there was nothing new. */
    public void recordSuccess() {
        jdbc.update(
                """
                INSERT INTO sync_state (name, last_success_at) VALUES (:name, NOW())
                ON CONFLICT (name) DO UPDATE SET last_success_at = NOW(), last_error = NULL, last_error_at = NULL
                """,
                Map.of("name", SYNC_NAME));
    }

    public void recordError(String message) {
        jdbc.update(
                """
                INSERT INTO sync_state (name, last_error, last_error_at) VALUES (:name, :error, NOW())
                ON CONFLICT (name) DO UPDATE SET last_error = EXCLUDED.last_error, last_error_at = NOW()
                """,
                Map.of("name", SYNC_NAME, "error", truncate(message)));
    }

    public Optional<SyncStatus> status() {
        return jdbc
                .query(
                        "SELECT cursor_value, last_success_at, last_error, last_error_at FROM sync_state WHERE name = :name",
                        Map.of("name", SYNC_NAME),
                        (rs, i) -> new SyncStatus(
                                rs.getString("cursor_value"),
                                rs.getObject("last_success_at", OffsetDateTime.class),
                                rs.getString("last_error"),
                                rs.getObject("last_error_at", OffsetDateTime.class)))
                .stream()
                .findFirst();
    }

    /** Counts one more failure of this post at this version and returns how many in a row. A new version starts again. */
    public int recordFailure(String postId, int version, String message) {
        var failures = jdbc.queryForObject(
                """
                INSERT INTO sync_failure (post_id, source_version, failures, last_error) VALUES (:id, :version, 1, :error)
                ON CONFLICT (post_id) DO UPDATE SET
                    failures = CASE WHEN sync_failure.source_version = EXCLUDED.source_version
                                    THEN sync_failure.failures + 1 ELSE 1 END,
                    source_version = EXCLUDED.source_version, last_error = EXCLUDED.last_error
                RETURNING failures
                """,
                Map.of("id", postId, "version", version, "error", truncate(message)),
                Integer.class);
        // an upsert that RETURNING always returns a row for; a missing one would be a database fault, not a count of
        // zero
        if (failures == null) throw new IllegalStateException("No failure count came back for post " + postId);
        return failures;
    }

    public void quarantine(String postId) {
        jdbc.update("UPDATE sync_failure SET quarantined_at = NOW() WHERE post_id = :id", Map.of("id", postId));
    }

    public void clearFailure(String postId) {
        jdbc.update("DELETE FROM sync_failure WHERE post_id = :id", Map.of("id", postId));
    }

    public List<String> quarantined() {
        return jdbc.queryForList(
                "SELECT post_id FROM sync_failure WHERE quarantined_at IS NOT NULL ORDER BY post_id",
                Map.of(),
                String.class);
    }

    public record StoredMedia(String contentType, byte[] content, OffsetDateTime fetchedAt) {
        public StoredMedia {
            content = content.clone();
        }

        @Override
        public byte[] content() {
            return content.clone();
        }
    }

    public record SyncStatus(
            String cursor, OffsetDateTime lastSuccessAt, String lastError, OffsetDateTime lastErrorAt) {}

    private static final String SELECT =
            "SELECT id, bucket, source_version, title, excerpt, cover_ref, category, body, published_at, updated_at FROM blog_post";

    private static final org.springframework.jdbc.core.RowMapper<BlogPost> MAPPER = (rs, i) -> new BlogPost(
            rs.getString("id"),
            rs.getString("bucket"),
            rs.getInt("source_version"),
            rs.getString("title"),
            rs.getString("excerpt"),
            rs.getString("cover_ref"),
            rs.getString("category"),
            rs.getString("body"),
            rs.getObject("published_at", OffsetDateTime.class),
            rs.getObject("updated_at", OffsetDateTime.class));

    private static Timestamp ts(OffsetDateTime value) {
        return value == null
                ? null
                : Timestamp.from(value.withOffsetSameInstant(ZoneOffset.UTC).toInstant());
    }

    private static String escapeLike(String value) {
        return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
    }

    private static String truncate(String message) {
        if (message == null) return null;
        return message.length() <= 1000 ? message : message.substring(0, 1000);
    }
}
