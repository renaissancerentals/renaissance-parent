package com.renaissancerentals.api.blog.web;

import com.renaissancerentals.api.blog.BlogPost;
import com.renaissancerentals.api.blog.BlogStore;
import com.renaissancerentals.api.blog.sync.BlogContentProcessor;
import com.renaissancerentals.api.error.NotFoundException;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The blog, read only from this site's own database. Responses are never served stale: a browser asks again each time
 * (answered 304 when nothing changed), so a post that is taken down is gone on the next page view and not a minute later.
 */
@RestController
@RequestMapping("/api/blog")
@RequiredArgsConstructor
public class BlogController {

    private static final int MAX_SIZE = 50;
    private static final CacheControl REVALIDATE = CacheControl.noCache().cachePublic();

    private final BlogStore store;
    private final BlogContentProcessor processor;

    @GetMapping("/posts")
    public ResponseEntity<BlogPage> posts(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "label", required = false) String label,
            @RequestParam(value = "size", defaultValue = "12") int size,
            @RequestParam(value = "pageToken", required = false) String pageToken) {
        int limit = Math.max(1, Math.min(size, MAX_SIZE));
        int offset = offset(pageToken);
        var found = store.list(query, label, limit + 1, offset);
        boolean more = found.size() > limit;
        var posts = (more ? found.subList(0, limit) : found)
                .stream().map(BlogSummary::from).toList();
        var body = new BlogPage(posts, more ? String.valueOf(offset + limit) : "");
        return ResponseEntity.ok()
                .cacheControl(REVALIDATE)
                .eTag(etag(posts, body.nextPageToken()))
                .body(body);
    }

    @GetMapping("/posts/{id}")
    public ResponseEntity<BlogDetail> post(@PathVariable("id") String id) {
        var post = store.find(id).orElseThrow(() -> new NotFoundException("Blog post " + id));
        var live = store.liveAmong(processor.linkedPosts(post.body()));
        var detail = BlogDetail.from(post, processor.resolveLinks(post.body(), live));
        return ResponseEntity.ok()
                .cacheControl(REVALIDATE)
                .eTag("\"" + post.sourceVersion() + "-" + live.hashCode() + "\"")
                .body(detail);
    }

    @GetMapping("/media/{bucket}/{mediaId}")
    public ResponseEntity<byte[]> media(
            @PathVariable("bucket") String bucket, @PathVariable("mediaId") String mediaId) {
        var media = store.media(bucket + "/" + mediaId)
                .orElseThrow(() -> new NotFoundException("Blog media " + bucket + "/" + mediaId));
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(Duration.ofDays(1)).cachePublic())
                .eTag("\"" + media.fetchedAt().toEpochSecond() + "\"")
                .header("X-Content-Type-Options", "nosniff")
                .contentType(MediaType.parseMediaType(media.contentType()))
                .body(media.content());
    }

    private static int offset(String token) {
        if (token == null || token.isBlank()) return 0;
        try {
            return Math.max(0, Integer.parseInt(token));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static String etag(List<BlogSummary> posts, String next) {
        return "\"" + posts.hashCode() + "-" + next + "\"";
    }

    /** The shape the site's pages already read: {@code blogs} and {@code nextPageToken}. */
    public record BlogPage(List<BlogSummary> blogs, String nextPageToken) {
        public BlogPage {
            blogs = blogs == null ? List.of() : List.copyOf(blogs);
        }
    }

    public record BlogSummary(String id, String title, String snippet, String img, String url, String category) {
        static BlogSummary from(BlogPost post) {
            return new BlogSummary(
                    post.id(),
                    post.title(),
                    post.excerpt(),
                    post.coverRef() == null ? null : BlogContentProcessor.MEDIA_PATH + post.coverRef(),
                    "/blogs/" + post.id(),
                    post.category());
        }
    }

    public record BlogDetail(
            String id, String title, String snippet, String img, String url, String category, String content) {
        static BlogDetail from(BlogPost post, String content) {
            var summary = BlogSummary.from(post);
            return new BlogDetail(
                    summary.id(),
                    summary.title(),
                    summary.snippet(),
                    summary.img(),
                    summary.url(),
                    summary.category(),
                    content);
        }
    }
}
