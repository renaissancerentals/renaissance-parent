package com.renaissancerentals.foundation.ratelimiter;

public interface RateLimiter {
    boolean shouldTrigger(String key);
}
