package com.renaissancerentals.foundation.ratelimiter;

import java.time.Instant;

public record RatelimiterEntry(Instant firstSeen, int count) {
}
