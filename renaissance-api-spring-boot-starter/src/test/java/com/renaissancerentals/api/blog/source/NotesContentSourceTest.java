package com.renaissancerentals.api.blog.source;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.renaissancerentals.api.blog.BlogProperties;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** The adapter against a small real HTTP server standing in for the notes platform. */
class NotesContentSourceTest {

    private HttpServer server;
    private final List<String> requests = new ArrayList<>();
    private final List<String> authorizations = new ArrayList<>();
    private final List<String> bodies = new ArrayList<>();
    private NotesContentSource source;

    @BeforeEach
    void start() throws IOException {
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/api/feed/changes", ex -> {
            record(ex);
            json(
                    ex,
                    200,
                    """
                    {"items":[{"id":"ab/abcd","version":2,"status":"PUBLISHED","at":"2026-10-09T10:00:00Z","title":"T",
                      "excerpt":"E","cover":"ab/img1","category":"Guides","publishedAt":"2026-10-09T10:00:00Z",
                      "updatedAt":"2026-10-09T10:00:00Z","contentHtml":"<p>x</p>","media":["ab/img1"]},
                     {"id":"ab/wxyz","version":3,"status":"REMOVED","at":"2026-10-09T10:00:00Z"}],
                     "nextCursor":"c9","hasMore":true}
                    """);
        });
        server.createContext("/api/feed/index", ex -> {
            record(ex);
            json(ex, 200, "[{\"id\":\"ab/abcd\",\"version\":2}]");
        });
        server.createContext("/api/feed/ack", ex -> {
            record(ex);
            bodies.add(new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            ex.sendResponseHeaders(204, -1);
            ex.close();
        });
        server.createContext("/api/media/ab/img1/content", ex -> {
            record(ex);
            ex.getResponseHeaders().add("Content-Type", "image/png");
            ex.sendResponseHeaders(200, 3);
            ex.getResponseBody().write(new byte[] {1, 2, 3});
            ex.close();
        });
        server.createContext("/api/media/ab/big/content", ex -> {
            record(ex);
            ex.getResponseHeaders().add("Content-Type", "image/png");
            ex.sendResponseHeaders(200, 5000);
            ex.getResponseBody().write(new byte[5000]);
            ex.close();
        });
        server.createContext("/api/media/ab/none/content", ex -> {
            record(ex);
            ex.sendResponseHeaders(404, -1);
            ex.close();
        });
        server.createContext("/api/media/ab/boom/content", ex -> {
            record(ex);
            ex.sendResponseHeaders(500, -1);
            ex.close();
        });
        server.start();
        var base = "http://127.0.0.1:" + server.getAddress().getPort() + "/";
        source = new NotesContentSource(
                new BlogProperties(new BlogProperties.Source(base, "tok-123", Duration.ofSeconds(5)), null));
    }

    @AfterEach
    void stop() {
        server.stop(0);
    }

    private void record(HttpExchange ex) {
        requests.add(ex.getRequestMethod() + " " + ex.getRequestURI());
        authorizations.add(ex.getRequestHeaders().getFirst("Authorization"));
    }

    private static void json(HttpExchange ex, int status, String body) throws IOException {
        var bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json");
        ex.sendResponseHeaders(status, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.close();
    }

    @Test
    void changesAreReadWithTheTokenTheCursorAndTheLimit() {
        var page = source.changes("c8", 25);

        assertThat(requests.get(0)).isEqualTo("GET /api/feed/changes?limit=25&cursor=c8");
        assertThat(authorizations.get(0)).isEqualTo("Bearer tok-123");
        assertThat(page.nextCursor()).isEqualTo("c9");
        assertThat(page.hasMore()).isTrue();
        assertThat(page.items().get(0).title()).isEqualTo("T");
        assertThat(page.items().get(0).media()).containsExactly("ab/img1");
        assertThat(page.items().get(1).removed()).isTrue();
    }

    @Test
    void theFirstRunAsksWithNoCursor() {
        source.changes(null, 10);

        assertThat(requests.get(0)).isEqualTo("GET /api/feed/changes?limit=10");
    }

    @Test
    void theIndexIsRead() {
        assertThat(source.index()).containsExactly(new SourceIndexEntry("ab/abcd", 2));
    }

    @Test
    void anAcknowledgementCarriesTheCursor() {
        source.acknowledge("c9");

        assertThat(requests.get(0)).isEqualTo("POST /api/feed/ack");
        assertThat(bodies.get(0)).contains("\"cursor\":\"c9\"");
    }

    @Test
    void mediaIsCopiedWithItsType() {
        var media = source.media("ab/img1", 1000).orElseThrow();

        assertThat(media.contentType()).isEqualTo("image/png");
        assertThat(media.content()).containsExactly(1, 2, 3);
    }

    @Test
    void mediaOverTheLimitOrMissingIsSkipped() {
        assertThat(source.media("ab/big", 1000)).isEmpty();
        assertThat(source.media("ab/none", 1000)).isEmpty();
    }

    @Test
    void aServerErrorOnMediaIsAFailureNotASilentSkip() {
        assertThatThrownBy(() -> source.media("ab/boom", 1000)).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void aReferenceThatCouldSteerTheRequestElsewhereIsNeverFollowed() {
        assertThat(source.media("../../etc/passwd", 1000)).isEmpty();
        assertThat(source.media("ab/img1/../x", 1000)).isEmpty();
        assertThat(requests).isEmpty();
    }
}
