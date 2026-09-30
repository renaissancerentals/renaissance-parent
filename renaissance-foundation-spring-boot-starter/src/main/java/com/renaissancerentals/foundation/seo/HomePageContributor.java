package com.renaissancerentals.foundation.seo;

import java.util.List;
import java.util.Optional;

/** Home page of a site that is not a single property (the hub, and the listing sites). */
public class HomePageContributor implements SeoPageContributor, SitemapContributor {

    private final SeoProperties properties;
    private final SiteLinks links;
    private final SeoJsonLd jsonLd;
    private final SnapshotBuilder snapshots;

    public HomePageContributor(SeoProperties properties, SiteLinks links, SeoJsonLd jsonLd, SnapshotBuilder snapshots) {
        this.snapshots = snapshots;
        this.properties = properties;
        this.links = links;
        this.jsonLd = jsonLd;
    }

    private boolean applies() {
        return SeoText.blank(properties.ownedPropertyId());
    }

    @Override
    public Optional<PageMeta> resolve(String path) {
        if (!applies() || !"/".equals(path)) {
            return Optional.empty();
        }
        var title = SeoText.blank(properties.defaultTitle())
                ? properties.siteName()
                : SeoText.plain(properties.defaultTitle());
        var description = SeoText.description(properties.defaultDescription(), properties.siteName());
        var ld = new java.util.ArrayList<java.util.Map<String, Object>>();
        ld.add(jsonLd.webSite());
        if (links.isSelf(links.hubOrigin())) {
            ld.add(jsonLd.organization());
        }
        var snapshot = links.isSelf(links.hubOrigin()) ? snapshots.build("communities", title, description) : null;
        if (snapshot == null) {
            snapshot = SnapshotBuilder.heading(title, description);
        }
        return Optional.of(new PageMeta(
                title,
                description,
                links.self("/"),
                true,
                200,
                "website",
                links.absolute(properties.defaultImage()),
                ld,
                snapshot));
    }

    @Override
    public List<SitemapEntry> entries() {
        if (!applies()) {
            return List.of();
        }
        return List.of(new SitemapEntry(links.self("/"), null, "weekly", 1.0, properties.siteName()));
    }
}
