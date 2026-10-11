package com.renaissancerentals.api.blog;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * The blog is a copy of the notes platform's published posts, kept in this site's own database
 * ({@code renaissancerentals.blog}). Pages read only from that copy, so the site's availability does not depend on the
 * notes server.
 *
 * @param source where the posts come from: the notes API and the token of this site's API account
 * @param sync how the copy is kept current
 */
@ConfigurationProperties(prefix = "renaissancerentals.blog")
public record BlogProperties(Source source, Sync sync) {

    public BlogProperties {
        source = source == null ? new Source(null, null, null) : source;
        sync = sync == null ? new Sync(null, null, null, null, null, null) : sync;
    }

    /**
     * @param baseUrl the notes API, such as {@code https://notes.example.com}; empty turns the sync off
     * @param token the API account's token (rotate before its yearly expiry)
     * @param timeout how long one request to the notes API may take
     */
    public record Source(String baseUrl, String token, Duration timeout) {
        public Source {
            timeout = timeout == null ? Duration.ofSeconds(20) : timeout;
        }

        public boolean configured() {
            return baseUrl != null && !baseUrl.isBlank() && token != null && !token.isBlank();
        }
    }

    /**
     * @param enabled whether this instance runs the scheduled sync (one instance should)
     * @param interval pause between runs; the webhook makes changes arrive sooner
     * @param batchSize posts asked for at a time
     * @param maxFailures failures of one post before it is set aside so the rest can go on
     * @param webhookSecret what the notes server sends to say "something changed"; empty refuses every call
     * @param maxMediaBytes the largest image or document copied
     */
    public record Sync(
            Boolean enabled,
            Duration interval,
            Integer batchSize,
            Integer maxFailures,
            String webhookSecret,
            Long maxMediaBytes) {
        public Sync {
            // wrapper types stay wrappers: a default is a wrapper too, so nothing is unboxed only to be boxed again
            enabled = enabled == null ? Boolean.FALSE : enabled;
            interval = interval == null ? Duration.ofMinutes(10) : interval;
            batchSize = batchSize == null ? Integer.valueOf(50) : batchSize;
            maxFailures = maxFailures == null ? Integer.valueOf(3) : maxFailures;
            webhookSecret = webhookSecret == null ? "" : webhookSecret;
            maxMediaBytes = maxMediaBytes == null ? Long.valueOf(10L * 1024 * 1024) : maxMediaBytes;
        }
    }
}
