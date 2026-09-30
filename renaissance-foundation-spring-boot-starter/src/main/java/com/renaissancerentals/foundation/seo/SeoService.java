package com.renaissancerentals.foundation.seo;

import java.time.Clock;
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Resolves page metadata and builds sitemap.xml, robots.txt and llms.txt for one site. */
public class SeoService {

    private static final Logger log = LoggerFactory.getLogger(SeoService.class);
    private static final int LLMS_MAX_LINKS = 200;

    private final SeoProperties properties;
    private final SiteLinks links;
    private final List<SeoPageContributor> pageContributors;
    private final List<SitemapContributor> sitemapContributors;
    private final TtlCache<String, PageMeta> pages;
    private final TtlCache<String, List<SitemapEntry>> sitemap;

    public SeoService(
            SeoProperties properties,
            SiteLinks links,
            List<SeoPageContributor> pageContributors,
            List<SitemapContributor> sitemapContributors,
            Clock clock) {
        this.properties = properties;
        this.links = links;
        this.pageContributors = pageContributors;
        this.sitemapContributors = sitemapContributors;
        this.pages = new TtlCache<>(Duration.ofMinutes(5), 5000, clock);
        this.sitemap = new TtlCache<>(Duration.ofMinutes(10), 1, clock);
    }

    public PageMeta resolve(String rawPath) {
        var path = SiteLinks.normalise(rawPath);
        return pages.get(path, () -> compute(path));
    }

    private PageMeta compute(String path) {
        for (var contributor : pageContributors) {
            var meta = contributor.resolve(path);
            if (meta.isPresent()) {
                return meta.get();
            }
        }
        // A route the SPA serves but nobody described: keep it out of the index rather than guess.
        var title = SeoText.blank(properties.defaultTitle()) ? properties.siteName() : properties.defaultTitle();
        return new PageMeta(
                SeoText.plain(title),
                SeoText.description(properties.defaultDescription(), properties.siteName()),
                links.self(path),
                false,
                200,
                "website",
                links.absolute(properties.defaultImage()),
                List.of(),
                null);
    }

    public List<SitemapEntry> entries() {
        return sitemap.get("all", () -> {
            var unique = new LinkedHashMap<String, SitemapEntry>();
            for (var contributor : sitemapContributors) {
                try {
                    contributor.entries().forEach(e -> unique.putIfAbsent(e.loc(), e));
                } catch (RuntimeException e) {
                    log.warn(
                            "Sitemap contributor {} failed: {}",
                            contributor.getClass().getSimpleName(),
                            e.getMessage());
                }
            }
            return List.copyOf(unique.values());
        });
    }

    public String sitemapXml() {
        var xml = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n")
                .append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        for (var entry : entries()) {
            xml.append("  <url><loc>").append(SeoText.esc(entry.loc())).append("</loc>");
            if (entry.lastmod() != null) {
                xml.append("<lastmod>").append(entry.lastmod()).append("</lastmod>");
            }
            if (entry.changefreq() != null) {
                xml.append("<changefreq>").append(entry.changefreq()).append("</changefreq>");
            }
            if (entry.priority() != null) {
                xml.append("<priority>")
                        .append(String.format(java.util.Locale.ROOT, "%.1f", entry.priority()))
                        .append("</priority>");
            }
            xml.append("</url>\n");
        }
        return xml.append("</urlset>\n").toString();
    }

    /**
     * Everything is crawlable. The API is deliberately not disallowed: pages render from it, and Google skips
     * resources that robots.txt blocks. JSON is kept out of the index with X-Robots-Tag instead.
     */
    public String robotsTxt() {
        return "User-agent: *\nAllow: /\n\nSitemap: " + links.origin() + "/sitemap.xml\n";
    }

    public String llmsTxt() {
        var out = new StringBuilder("# ").append(properties.siteName()).append("\n\n");
        if (!SeoText.blank(properties.defaultDescription())) {
            out.append("> ")
                    .append(SeoText.plain(properties.defaultDescription()))
                    .append("\n\n");
        }
        var org = properties.organization();
        var contact = new LinkedHashMap<String, String>();
        contact.put("Phone", SeoText.phone(org.telephone()));
        contact.put("Email", org.email());
        if (!SeoText.blank(org.streetAddress())) {
            contact.put(
                    "Address",
                    org.streetAddress() + ", " + org.locality() + ", " + org.region()
                            + (SeoText.blank(org.postalCode()) ? "" : " " + org.postalCode()));
        }
        var lines = contact.entrySet().stream()
                .filter(e -> !SeoText.blank(e.getValue()))
                .map((Map.Entry<String, String> e) -> "- " + e.getKey() + ": " + e.getValue())
                .toList();
        if (!lines.isEmpty()) {
            out.append("## Contact\n").append(String.join("\n", lines)).append("\n\n");
        }
        out.append("## Pages\n");
        entries().stream()
                .filter(e -> !SeoText.blank(e.title()))
                .limit(LLMS_MAX_LINKS)
                .forEach(e -> out.append("- [")
                        .append(SeoText.plain(e.title()).replace("]", ")").replace("[", "("))
                        .append("](")
                        .append(e.loc())
                        .append(")\n"));
        return out.toString();
    }
}
