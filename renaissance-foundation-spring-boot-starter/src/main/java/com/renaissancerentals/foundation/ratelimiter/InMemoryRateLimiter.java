package com.renaissancerentals.foundation.ratelimiter;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.scheduling.annotation.Scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class InMemoryRateLimiter implements RateLimiter {

    private final Duration window;
    private final int threshold;

    private final ConcurrentHashMap<String, RatelimiterEntry> state = new ConcurrentHashMap<>();

    @Override
    public boolean shouldTrigger(String key){

        final var now = Instant.now();

        return state.compute(key,(k,ratelimiterEntry) -> {
            if (ratelimiterEntry == null || ratelimiterEntry.firstSeen().isBefore(now.minus(window))) {
                return new RatelimiterEntry(now, 1);
            }

            int currentCount = ratelimiterEntry.count() + 1;

            if (currentCount >= threshold) {
                return new RatelimiterEntry(now, 0);
            }
            return new RatelimiterEntry(ratelimiterEntry.firstSeen(), currentCount);

        }).count() == 0;

    }

    @Scheduled(fixedDelayString = "${rate-limiter.cleanup-interval:600000}")
    public void cleanup(){
        final var now = Instant.now();
        log.debug("Rate limiter cleanup started...");
        state.entrySet().removeIf(entry -> entry.getValue().firstSeen().isBefore(now.minus(window)));
    }
}
