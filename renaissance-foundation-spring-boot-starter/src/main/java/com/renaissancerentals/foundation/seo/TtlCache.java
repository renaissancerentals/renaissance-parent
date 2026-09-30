package com.renaissancerentals.foundation.seo;

import java.time.Clock;
import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/** Tiny bounded in-memory cache. Failures are never cached (the loader throws). */
final class TtlCache<K, V> {

    private record Entry<V>(V value, long expiresAtMillis) {}

    private final ConcurrentHashMap<K, Entry<V>> entries = new ConcurrentHashMap<>();
    private final Duration ttl;
    private final int maxEntries;
    private final Clock clock;

    TtlCache(Duration ttl, int maxEntries, Clock clock) {
        this.ttl = ttl;
        this.maxEntries = maxEntries;
        this.clock = clock;
    }

    V get(K key, Supplier<V> loader) {
        var now = clock.millis();
        var cached = entries.get(key);
        if (cached != null && cached.expiresAtMillis() > now) {
            return cached.value();
        }
        var loaded = loader.get();
        if (entries.size() >= maxEntries) {
            entries.entrySet().removeIf(e -> e.getValue().expiresAtMillis() <= now);
            if (entries.size() >= maxEntries) {
                entries.clear();
            }
        }
        entries.put(key, new Entry<>(loaded, now + ttl.toMillis()));
        return loaded;
    }
}
