package com.renaissancerentals.api.blog.web;

import com.renaissancerentals.api.blog.BlogProperties;
import com.renaissancerentals.api.blog.BlogStore;
import com.renaissancerentals.api.blog.sync.BlogSyncService;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.concurrent.ExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * How the notes server says "something changed", so that new and changed posts arrive in seconds and a post that was taken
 * down comes down at once. It carries no content and trusts nothing: it only starts the same sync the timer runs, which
 * reads from the notes server itself. A missed call costs nothing but time. Guarded by a shared secret; with none set, every
 * call is refused.
 */
@Slf4j
@RestController
@RequestMapping("/api/blog/sync")
@ConditionalOnExpression("'${renaissancerentals.blog.source.base-url:}' != ''")
public class BlogSyncWebhookController {

    static final String SECRET_HEADER = "X-Sync-Secret";

    private final BlogSyncService service;
    private final BlogStore store;
    private final BlogProperties properties;
    private final ExecutorService executor;

    public BlogSyncWebhookController(
            BlogSyncService service,
            BlogStore store,
            BlogProperties properties,
            ExecutorService virtualThreadExecutor) {
        this.service = service;
        this.store = store;
        this.properties = properties;
        this.executor = virtualThreadExecutor;
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> changed(@RequestHeader(value = SECRET_HEADER, required = false) String secret) {
        if (!allowed(secret))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        executor.execute(() -> {
            try {
                service.sync();
            } catch (RuntimeException e) {
                // recorded by the service
            }
        });
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/status")
    public ResponseEntity<BlogStore.SyncStatus> status(
            @RequestHeader(value = SECRET_HEADER, required = false) String secret) {
        if (!allowed(secret))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        return ResponseEntity.of(store.status());
    }

    private boolean allowed(String given) {
        String expected = properties.sync().webhookSecret();
        if (expected.isBlank() || given == null) return false;
        return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), given.getBytes(StandardCharsets.UTF_8));
    }
}
