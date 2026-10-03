package com.renaissancerentals.foundation.seo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.util.AntPathMatcher;

/** Routes that are listed in configuration. Exact paths win over patterns. */
public class StaticPageContributor implements SeoPageContributor, SitemapContributor {

    private final SeoProperties properties;
    private final SiteLinks links;
    private final SnapshotBuilder snapshots;
    private final SeoJsonLd jsonLd;
    private final AntPathMatcher matcher = new AntPathMatcher();

    public StaticPageContributor(
            SeoProperties properties, SiteLinks links, SnapshotBuilder snapshots, SeoJsonLd jsonLd) {
        this.jsonLd = jsonLd;
        this.properties = properties;
        this.links = links;
        this.snapshots = snapshots;
    }

    @Override
    public Optional<PageMeta> resolve(String path) {
        return properties.pages().stream()
                .filter(page -> page.path() != null && matcher.match(SiteLinks.normalise(page.path()), path))
                .min(Comparator.comparing((SeoProperties.StaticPage page) ->
                        !SiteLinks.normalise(page.path()).equals(path)))
                .map(page -> toMeta(page, path));
    }

    private PageMeta toMeta(SeoProperties.StaticPage page, String path) {
        var canonical = page.canonical() == null ? links.self(path) : links.absolute(page.canonical());
        var title = SeoText.blank(page.title()) ? properties.siteName() : SeoText.plain(page.title());
        var description = SeoText.description(page.description(), properties.defaultDescription());
        return new PageMeta(
                title,
                description,
                canonical,
                page.indexable(),
                200,
                "website",
                links.absolute(properties.defaultImage()),
                structuredData(page, path, canonical, title, description),
                snapshot(page, title, description));
    }

    /** Only pages that are indexed under their own URL get markup; a page that points elsewhere would contradict it. */
    private List<Map<String, Object>> structuredData(
            SeoProperties.StaticPage page, String path, String canonical, String title, String description) {
        if (!page.indexable() || !links.isSelf(canonical) || !canonical.equals(links.self(path))) {
            return List.of();
        }
        var type = SeoText.blank(page.snapshot()) ? "WebPage" : "CollectionPage";
        var nodes = new ArrayList<Map<String, Object>>();
        nodes.add(jsonLd.webPage(type, title, canonical, description));
        var wildcard = page.path().contains("*") || page.path().contains("{");
        if (!wildcard && !"/".equals(path)) {
            nodes.add(jsonLd.breadcrumbs(
                    List.of(new String[] {properties.siteName(), links.self("/")}, new String[] {title, canonical})));
        }
        return nodes;
    }

    private String snapshot(SeoProperties.StaticPage page, String title, String description) {
        var built = snapshots.build(page.snapshot(), title, description);
        return built != null ? built : SnapshotBuilder.heading(title, description);
    }

    @Override
    public List<SitemapEntry> entries() {
        return properties.pages().stream()
                .filter(SeoProperties.StaticPage::listedInSitemap)
                .map(page ->
                        new SitemapEntry(links.self(page.path()), null, "monthly", 0.5, SeoText.plain(page.title())))
                .toList();
    }
}
