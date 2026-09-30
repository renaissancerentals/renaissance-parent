package com.renaissancerentals.foundation.seo;

import java.util.regex.Pattern;

/**
 * Decides which site owns which content and builds canonical URLs. A property's own site owns that property's home
 * page and floorplans; the hub owns everything else (units, sublets, jobs, properties that have no site).
 */
public class SiteLinks {

    private static final Pattern MULTI_SLASH = Pattern.compile("/{2,}");

    private final SeoProperties properties;

    public SiteLinks(SeoProperties properties) {
        this.properties = properties;
    }

    public String origin() {
        return properties.canonicalOrigin();
    }

    public String hubOrigin() {
        return properties.hubOrigin();
    }

    public boolean isSelf(String absoluteUrl) {
        return absoluteUrl != null && (absoluteUrl.equals(origin()) || absoluteUrl.startsWith(origin() + "/"));
    }

    /** Absolute URL on this site. Root keeps its slash, other paths lose a trailing one. */
    public String self(String path) {
        return origin() + normalise(path);
    }

    public String absolute(String pathOrUrl) {
        if (pathOrUrl == null || pathOrUrl.isBlank()) {
            return null;
        }
        if (pathOrUrl.startsWith("http://") || pathOrUrl.startsWith("https://")) {
            return pathOrUrl;
        }
        return self(pathOrUrl.startsWith("/") ? pathOrUrl : "/" + pathOrUrl);
    }

    public static String normalise(String path) {
        if (path == null || path.isBlank()) {
            return "/";
        }
        var cleaned =
                MULTI_SLASH.matcher(path.startsWith("/") ? path : "/" + path).replaceAll("/");
        return cleaned.length() > 1 && cleaned.endsWith("/") ? cleaned.substring(0, cleaned.length() - 1) : cleaned;
    }

    private String propertySite(String propertyId) {
        if (propertyId == null) {
            return null;
        }
        if (propertyId.equals(properties.ownedPropertyId())
                || properties.additionalPropertyIds().contains(propertyId)) {
            return origin();
        }
        return properties.preferPropertySiteCanonical()
                ? properties.propertySites().get(propertyId)
                : null;
    }

    public boolean ownsPropertySite(String propertyId) {
        return propertySite(propertyId) != null;
    }

    /** Home page of a property's own site, otherwise its page on the hub. */
    public String propertyUrl(String propertyId) {
        var site = propertySite(propertyId);
        return site != null ? site + "/" : hubOrigin() + "/properties/" + propertyId;
    }

    public String floorplanUrl(String propertyId, String floorplanId) {
        var site = propertySite(propertyId);
        return (site != null ? site : hubOrigin()) + "/floorplans/" + floorplanId;
    }

    public String unitUrl(String unitId) {
        return hubOrigin() + "/units/" + unitId;
    }

    public String subletUrl(String assetKey) {
        return hubOrigin() + "/sublets/" + assetKey;
    }

    public String jobUrl(long id) {
        return hubOrigin() + "/employment/" + id;
    }
}
