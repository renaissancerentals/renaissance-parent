package com.renaissancerentals.api.cache;

import java.time.Duration;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Browser/proxy caching of the read-only API responses ({@code renaissancerentals.api.cache}).
 *
 * @param enabled switch for the whole feature (default on)
 * @param maxAge how long a response is fresh (default 60 seconds)
 * @param staleWhileRevalidate how long a stale response may be shown while a fresh one is fetched (default 2 minutes)
 * @param paths path prefixes of GET endpoints that carry no personal data and change rarely. Sublets are left out
 *     on purpose: the list changes as people post and delete, and it contains contact details.
 */
@ConfigurationProperties(prefix = "renaissancerentals.api.cache")
public record ApiCacheProperties(Boolean enabled, Duration maxAge, Duration staleWhileRevalidate, List<String> paths) {

    public static final List<String> DEFAULT_PATHS = List.of(
            "/api/properties",
            "/api/floorplans",
            "/api/units",
            "/api/shortTermFloorplans",
            "/api/leasingOffices",
            "/api/teamMembers",
            "/api/faqs",
            "/api/homePageSpecials",
            "/api/jobVacancies",
            "/api/folders",
            "/api/videos");

    public ApiCacheProperties {
        enabled = enabled == null || enabled;
        maxAge = maxAge == null ? Duration.ofSeconds(60) : maxAge;
        staleWhileRevalidate = staleWhileRevalidate == null ? Duration.ofMinutes(2) : staleWhileRevalidate;
        paths = List.copyOf(paths == null || paths.isEmpty() ? DEFAULT_PATHS : paths);
    }
}
