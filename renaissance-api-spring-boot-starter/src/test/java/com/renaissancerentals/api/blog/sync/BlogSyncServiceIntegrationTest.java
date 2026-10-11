package com.renaissancerentals.api.blog.sync;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.renaissancerentals.api.blog.BlogProperties;
import com.renaissancerentals.api.blog.BlogStore;
import com.renaissancerentals.api.blog.source.ContentSource;
import com.renaissancerentals.api.blog.source.SourceIndexEntry;
import com.renaissancerentals.api.blog.source.SourceItem;
import com.renaissancerentals.api.blog.source.SourceMedia;
import com.renaissancerentals.api.blog.source.SourcePage;
import com.renaissancerentals.api.support.PostgresIntegrationTest;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

/** The sync against a real database and a scripted source: the properties that keep the copy right. */
class BlogSyncServiceIntegrationTest extends PostgresIntegrationTest {

    private static final OffsetDateTime AT = OffsetDateTime.parse("2026-10-09T10:00:00Z");

    @Autowired
    private NamedParameterJdbcTemplate named;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private PlatformTransactionManager transactions;

    private BlogStore store;
    private ScriptedSource source;
    private BlogSyncService service;

    @BeforeEach
    void setUp() {
        jdbc.update("DELETE FROM blog_post");
        jdbc.update("DELETE FROM blog_media");
        jdbc.update("DELETE FROM sync_state");
        jdbc.update("DELETE FROM sync_failure");
        store = new BlogStore(named);
        source = new ScriptedSource();
        service = new BlogSyncService(
                source, store, new BlogContentProcessor(), props(3), new TransactionTemplate(transactions));
    }

    private static BlogProperties props(int maxFailures) {
        return new BlogProperties(null, new BlogProperties.Sync(true, null, 2, maxFailures, "secret", 1000L));
    }

    private static SourceItem post(String id, int version, String title) {
        return new SourceItem(
                id,
                version,
                "PUBLISHED",
                AT,
                title,
                "Excerpt",
                "ab/img1",
                "Guides",
                AT,
                AT,
                "<p>" + title + "</p><figure data-media-image-src=\"ab/img1\"><img src=\"x\"/></figure>",
                List.of("ab/img1"));
    }

    private static SourceItem gone(String id, int version) {
        return new SourceItem(id, version, "REMOVED", AT, null, null, null, null, null, null, null, List.of());
    }

    @Test
    void aFirstRunStoresPostsTheirImagesAndTheCursorThenAcknowledges() {
        source.pages.add(new SourcePage(List.of(post("ab/abcd", 1, "First")), "c1", false));

        var result = service.sync();

        assertThat(result.applied()).isEqualTo(1);
        assertThat(store.find("abcd")).get().satisfies(p -> {
            assertThat(p.title()).isEqualTo("First");
            assertThat(p.body()).contains("/api/blog/media/ab/img1");
        });
        assertThat(store.media("ab/img1")).isPresent();
        assertThat(store.cursor()).contains("c1");
        assertThat(source.acknowledged).containsExactly("c1");
        assertThat(store.status()).get().satisfies(s -> assertThat(s.lastSuccessAt())
                .isNotNull());
    }

    @Test
    void aSecondRunWithTheSamePageChangesNothing() {
        source.pages.add(new SourcePage(List.of(post("ab/abcd", 1, "First")), "c1", false));
        source.pages.add(new SourcePage(List.of(post("ab/abcd", 1, "First")), "c1", false));
        service.sync();

        var again = service.sync();

        assertThat(again.applied()).isZero();
        assertThat(store.list(null, null, 10, 0)).hasSize(1);
    }

    @Test
    void anOlderVersionNeverOverwritesANewerOne() {
        source.pages.add(new SourcePage(List.of(post("ab/abcd", 5, "Newer")), "c1", false));
        source.pages.add(new SourcePage(List.of(post("ab/abcd", 3, "Older")), "c2", false));
        service.sync();

        service.sync();

        assertThat(store.find("abcd")).get().extracting(p -> p.title()).isEqualTo("Newer");
    }

    @Test
    void aTombstoneTakesThePostDownAndDropsItsText() {
        source.pages.add(new SourcePage(List.of(post("ab/abcd", 1, "First")), "c1", false));
        source.pages.add(new SourcePage(List.of(gone("ab/abcd", 1)), "c2", false));
        service.sync();

        var result = service.sync();

        assertThat(result.removed()).isEqualTo(1);
        assertThat(store.find("abcd")).isEmpty();
        assertThat(jdbc.queryForObject("SELECT body FROM blog_post WHERE id = 'abcd'", String.class))
                .isEmpty();
    }

    @Test
    void aPostPublishedAgainAtTheSameVersionComesBack() {
        source.pages.add(new SourcePage(List.of(post("ab/abcd", 1, "First")), "c1", false));
        source.pages.add(new SourcePage(List.of(gone("ab/abcd", 1)), "c2", false));
        source.pages.add(new SourcePage(List.of(post("ab/abcd", 1, "First")), "c3", false));
        service.sync();
        service.sync();

        service.sync();

        assertThat(store.find("abcd")).isPresent();
    }

    @Test
    void anOlderPostCannotBringBackOneRemovedAtANewerVersion() {
        source.pages.add(new SourcePage(List.of(post("ab/abcd", 2, "Second")), "c1", false));
        source.pages.add(new SourcePage(List.of(gone("ab/abcd", 2)), "c2", false));
        source.pages.add(new SourcePage(List.of(post("ab/abcd", 1, "Stale")), "c3", false));
        service.sync();
        service.sync();

        service.sync();

        assertThat(store.find("abcd")).isEmpty();
    }

    @Test
    void severalPagesAreAllAppliedAndEachCursorIsAcknowledged() {
        source.pages.add(new SourcePage(List.of(post("ab/aaaa", 1, "A"), post("ab/bbbb", 1, "B")), "c1", true));
        source.pages.add(new SourcePage(List.of(post("ab/cccc", 1, "C")), "c2", false));

        var result = service.sync();

        assertThat(result.applied()).isEqualTo(3);
        assertThat(source.acknowledged).containsExactly("c1", "c2");
        assertThat(store.cursor()).contains("c2");
    }

    @Test
    void aFailureLeavesTheCursorWhereItWasSoTheNextRunRepeats() {
        source.pages.add(new SourcePage(List.of(post("ab/abcd", 1, "First")), "c1", false));
        source.mediaFails = true;

        assertThatThrownBy(() -> service.sync()).isInstanceOf(IllegalStateException.class);

        assertThat(store.cursor()).isEmpty();
        assertThat(store.find("abcd")).isEmpty();
        assertThat(store.status()).get().satisfies(s -> assertThat(s.lastError())
                .contains("media"));
    }

    @Test
    void aPostThatKeepsFailingIsSetAsideSoTheRestGoOn() {
        source.mediaFails = true;
        var bad = post("ab/bad1", 1, "Bad");
        var good = new SourceItem(
                "ab/good", 1, "PUBLISHED", AT, "Good", "e", null, "Guides", AT, AT, "<p>ok</p>", List.of());
        for (int i = 0; i < 3; i++) {
            source.pages.add(new SourcePage(List.of(bad, good), "c" + (i + 1), false));
        }
        assertThatThrownBy(() -> service.sync()).isInstanceOf(IllegalStateException.class);
        assertThatThrownBy(() -> service.sync()).isInstanceOf(IllegalStateException.class);

        var result = service.sync();

        assertThat(result.setAside()).isEqualTo(1);
        assertThat(store.find("good")).isPresent();
        assertThat(store.find("bad1")).isEmpty();
        assertThat(store.quarantined()).containsExactly("bad1");
        assertThat(store.cursor()).contains("c3");
    }

    @Test
    void anAcknowledgementThatFailsDoesNotUndoTheSync() {
        source.pages.add(new SourcePage(List.of(post("ab/abcd", 1, "First")), "c1", false));
        source.ackFails = true;

        var result = service.sync();

        assertThat(result.applied()).isEqualTo(1);
        assertThat(store.find("abcd")).isPresent();
    }

    @Test
    void reconcileTakesDownWhatIsNoLongerPublicAndKeepsItsVersionSoItCanReturn() {
        source.pages.add(new SourcePage(List.of(post("ab/abcd", 4, "First")), "c1", false));
        service.sync();
        source.index = List.of();

        var result = service.reconcile();

        assertThat(result.removed()).isEqualTo(1);
        assertThat(store.find("abcd")).isEmpty();
        assertThat(jdbc.queryForObject("SELECT source_version FROM blog_post WHERE id = 'abcd'", Integer.class))
                .isEqualTo(4);
    }

    @Test
    void reconcileFetchesFromTheBeginningWhenSomethingPublicIsMissing() {
        source.index = List.of(new SourceIndexEntry("ab/abcd", 1));
        source.pages.add(new SourcePage(List.of(post("ab/abcd", 1, "First")), "c1", false));

        var result = service.reconcile();

        assertThat(result.applied()).isEqualTo(1);
        assertThat(store.find("abcd")).isPresent();
        assertThat(source.askedFrom.get(0)).isNull();
    }

    @Test
    void reconcileDoesNothingWhenTheCopyAlreadyMatches() {
        source.pages.add(new SourcePage(List.of(post("ab/abcd", 1, "First")), "c1", false));
        service.sync();
        source.index = List.of(new SourceIndexEntry("ab/abcd", 1));
        source.askedFrom.clear();

        var result = service.reconcile();

        assertThat(result.applied()).isZero();
        assertThat(source.askedFrom).isEmpty();
    }

    @Test
    void theListIsNewestFirstAndFindsWords() {
        source.pages.add(new SourcePage(
                List.of(
                        new SourceItem(
                                "ab/aaaa",
                                1,
                                "PUBLISHED",
                                AT,
                                "Old guide",
                                "e",
                                null,
                                "Guides",
                                AT.minusDays(5),
                                AT,
                                "<p>leases</p>",
                                List.of()),
                        new SourceItem(
                                "ab/bbbb",
                                1,
                                "PUBLISHED",
                                AT,
                                "New guide",
                                "e",
                                null,
                                "News",
                                AT,
                                AT,
                                "<p>parking</p>",
                                List.of())),
                "c1",
                false));
        service.sync();

        assertThat(store.list(null, null, 10, 0)).extracting(p -> p.id()).containsExactly("bbbb", "aaaa");
        assertThat(store.list("PARKING", null, 10, 0)).extracting(p -> p.id()).containsExactly("bbbb");
        assertThat(store.list(null, "guides", 10, 0)).extracting(p -> p.id()).containsExactly("aaaa");
        assertThat(store.list("100%", null, 10, 0)).isEmpty();
    }

    /** A source that hands out the pages it was given, in order, and remembers what it was asked. */
    private static class ScriptedSource implements ContentSource {
        final List<SourcePage> pages = new ArrayList<>();
        final List<String> acknowledged = new ArrayList<>();
        final List<String> askedFrom = new ArrayList<>();
        List<SourceIndexEntry> index = List.of();
        boolean mediaFails;
        boolean ackFails;

        @Override
        public SourcePage changes(String cursor, int limit) {
            askedFrom.add(cursor);
            return pages.isEmpty()
                    ? new SourcePage(List.of(), cursor == null ? "start" : cursor, false)
                    : pages.remove(0);
        }

        @Override
        public List<SourceIndexEntry> index() {
            return index;
        }

        @Override
        public void acknowledge(String cursor) {
            if (ackFails) throw new IllegalStateException("ack down");
            acknowledged.add(cursor);
        }

        @Override
        public Optional<SourceMedia> media(String ref, long maxBytes) {
            if (mediaFails) throw new IllegalStateException("media down");
            return Optional.of(new SourceMedia("image/png", new byte[] {1, 2, 3}));
        }
    }
}
