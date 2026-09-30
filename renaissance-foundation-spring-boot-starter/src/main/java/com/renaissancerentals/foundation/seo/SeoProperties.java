package com.renaissancerentals.foundation.seo;

import java.util.List;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Per-site SEO/GEO configuration. The SEO support is switched on by setting {@code canonical-origin}.
 *
 * @param canonicalOrigin the one primary origin of this site, e.g. {@code https://www.highgrovebloomington.com}
 * @param siteName brand used in generated titles and social cards
 * @param defaultTitle title used for the home page / fallbacks
 * @param defaultDescription description used for the home page / fallbacks
 * @param defaultImage social image (path or absolute URL)
 * @param ownedPropertyId set on single-property sites, e.g. {@code high-grove}
 * @param additionalPropertyIds further property ids that are shown on this site, e.g. a short term variant
 * @param hubOrigin origin of the main site that owns everything that has no site of its own
 * @param dataBaseUrls hosts to read listing data from when this site has no database of its own (failover order)
 * @param propertySites property id to the origin of that property's own site
 * @param preferPropertySiteCanonical when true a property's own site is canonical for that property's content
 * @param organization business details used for schema.org markup
 * @param pages explicit list of routes that are served by the single page app
 */
@ConfigurationProperties(prefix = "renaissancerentals.seo")
public record SeoProperties(
        String canonicalOrigin,
        String siteName,
        String defaultTitle,
        String defaultDescription,
        String defaultImage,
        String ownedPropertyId,
        List<String> additionalPropertyIds,
        String hubOrigin,
        List<String> dataBaseUrls,
        Map<String, String> propertySites,
        Boolean preferPropertySiteCanonical,
        Organization organization,
        List<StaticPage> pages) {

    public static final String DEFAULT_HUB_ORIGIN = "https://www.renaissancerentals.com";

    public static final Map<String, String> DEFAULT_PROPERTY_SITES = Map.of(
            "covenanter-hill", "https://www.covenanterhill.com",
            "high-grove", "https://www.highgrovebloomington.com",
            "scholars-quad", "https://www.scholarsquad.com",
            "scholars-rooftop", "https://www.scholarsrooftop.com",
            "summer-house", "https://www.summerhouseatindiana.com",
            "summer-house-short-term", "https://www.summerhouseatindiana.com",
            "verona-park", "https://www.veronaparkneighborhood.com");

    public SeoProperties {
        canonicalOrigin = stripTrailingSlash(canonicalOrigin);
        siteName = isBlank(siteName) ? "Renaissance Rentals" : siteName;
        additionalPropertyIds = additionalPropertyIds == null ? List.of() : List.copyOf(additionalPropertyIds);
        hubOrigin = isBlank(hubOrigin) ? DEFAULT_HUB_ORIGIN : stripTrailingSlash(hubOrigin);
        dataBaseUrls = dataBaseUrls == null || dataBaseUrls.isEmpty()
                ? List.of(DEFAULT_HUB_ORIGIN, "https://www.scholarsrooftop.com")
                : List.copyOf(dataBaseUrls.stream()
                        .map(SeoProperties::stripTrailingSlash)
                        .toList());
        propertySites =
                Map.copyOf(propertySites == null || propertySites.isEmpty() ? DEFAULT_PROPERTY_SITES : propertySites);
        preferPropertySiteCanonical = preferPropertySiteCanonical == null || preferPropertySiteCanonical;
        organization = organization == null
                ? new Organization(null, null, null, null, null, null, null, null, null, null)
                : organization;
        pages = pages == null ? List.of() : List.copyOf(pages);
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private static String stripTrailingSlash(String value) {
        if (value == null) {
            return null;
        }
        var trimmed = value.trim();
        while (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

    public record Organization(
            String name,
            String telephone,
            String email,
            String streetAddress,
            String locality,
            String region,
            String postalCode,
            String country,
            String logo,
            List<String> sameAs) {
        public Organization {
            locality = locality == null ? "Bloomington" : locality;
            region = region == null ? "IN" : region;
            country = country == null ? "US" : country;
            sameAs = sameAs == null ? List.of() : List.copyOf(sameAs);
        }
    }

    /**
     * A route served by the single page app.
     *
     * @param path Ant style path, e.g. {@code /contact} or {@code /blogs/**}
     * @param title page title
     * @param description meta description
     * @param indexable false renders {@code noindex}
     * @param sitemap whether the exact path is listed in sitemap.xml (defaults to indexable and no wildcard)
     * @param canonical optional canonical override, a path on this site or an absolute URL
     * @param snapshot optional crawler text built from listing data: floorplans, units, sublets or jobs
     */
    public record StaticPage(
            String path,
            String title,
            String description,
            Boolean indexable,
            Boolean sitemap,
            String canonical,
            String snapshot) {
        public StaticPage {
            indexable = indexable == null || indexable;
        }

        public boolean listedInSitemap() {
            var wildcard = path != null && (path.contains("*") || path.contains("{"));
            return sitemap != null ? sitemap : indexable && !wildcard && canonical == null;
        }
    }
}
