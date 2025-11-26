package com.renaissancerentals.foundation.ratelimiter;

import java.time.Instant;

public record RateLimiterEntry(Instant firstSeen, int count) {
}
