package com.renaissancerentals.foundation.seo;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Crawler-readable text for list pages, whose content is otherwise only produced by JavaScript. Bounded, so a page
 * never grows by more than a few dozen kilobytes.
 */
public class SnapshotBuilder {

    private static final Logger log = LoggerFactory.getLogger(SnapshotBuilder.class);
    private static final int MAX_FLOORPLANS = 250;
    private static final int MAX_UNITS = 150;
    private static final int MAX_SUBLETS = 100;

    private final SiteLinks links;
    private final SeoDataSource data;

    public SnapshotBuilder(SiteLinks links, SeoDataSource data) {
        this.links = links;
        this.data = data;
    }

    /** @param kind floorplans, units, sublets, jobs or communities. Unknown kinds and outages give null. */
    public String build(String kind, String title, String description) {
        if (SeoText.blank(kind)) {
            return null;
        }
        try {
            var list =
                    switch (kind.trim().toLowerCase(java.util.Locale.ROOT)) {
                        case "floorplans" -> floorplans();
                        case "units" -> units();
                        case "sublets" -> sublets();
                        case "jobs" -> jobs();
                        case "communities" -> communities();
                        default -> null;
                    };
            return list == null ? null : heading(title, description) + list;
        } catch (SeoDataUnavailableException e) {
            log.warn("Snapshot '{}' unavailable: {}", kind, e.getMessage());
            return null;
        }
    }

    static String heading(String title, String description) {
        return "<h1>" + SeoText.esc(SeoText.plain(title)) + "</h1><p>" + SeoText.esc(SeoText.plain(description))
                + "</p>";
    }

    private static String bedBath(Integer bedroom, Float bathroom) {
        var parts = new ArrayList<String>();
        if (bedroom != null) parts.add(bedroom == 0 ? "studio" : bedroom + " bed");
        if (bathroom != null) {
            parts.add((bathroom == Math.rint(bathroom) ? String.valueOf(bathroom.intValue()) : String.valueOf(bathroom))
                    + " bath");
        }
        return String.join(", ", parts);
    }

    private static Float lowestRent(List<SeoData.ListingUnit> units) {
        return units.stream()
                .map(SeoData.ListingUnit::rent)
                .filter(rent -> rent != null && rent > 0)
                .min(Float::compare)
                .orElse(null);
    }

    private String floorplans() {
        var html = new StringBuilder();
        var shown = 0;
        for (var listing : data.listings()) {
            if (listing.floorplans().isEmpty() || shown >= MAX_FLOORPLANS) {
                continue;
            }
            html.append("<h2><a href=\"")
                    .append(SeoText.esc(links.propertyUrl(listing.id())))
                    .append("\">")
                    .append(SeoText.esc(listing.name()))
                    .append("</a></h2><ul>");
            for (var fp : listing.floorplans()) {
                if (shown++ >= MAX_FLOORPLANS) {
                    break;
                }
                html.append("<li><a href=\"")
                        .append(SeoText.esc(links.floorplanUrl(listing.id(), fp.id())))
                        .append("\">")
                        .append(SeoText.esc(fp.name()))
                        .append("</a>");
                var facts = new ArrayList<String>();
                var size = bedBath(fp.bedroom(), fp.bathroom());
                if (!size.isEmpty()) facts.add(size);
                var rent = lowestRent(fp.units());
                if (rent != null) facts.add("from " + SeoFacts.money(rent) + " per month");
                if (!fp.units().isEmpty()) facts.add(fp.units().size() + " available");
                if (!facts.isEmpty())
                    html.append(" (")
                            .append(SeoText.esc(String.join(", ", facts)))
                            .append(")");
                html.append("</li>");
            }
            html.append("</ul>");
        }
        return html.toString();
    }

    private record UnitLine(String propertyName, SeoData.ListingFloorplan floorplan, SeoData.ListingUnit unit) {}

    /** Cheapest first; units without a rent go last. */
    private static double sortRent(UnitLine line) {
        var rent = line.unit().rent();
        return rent == null || rent <= 0 ? Double.MAX_VALUE : rent;
    }

    private String units() {
        var lines = new ArrayList<UnitLine>();
        for (var listing : data.listings()) {
            for (var fp : listing.floorplans()) {
                for (var unit : fp.units()) {
                    lines.add(new UnitLine(listing.name(), fp, unit));
                }
            }
        }
        lines.sort(Comparator.comparingDouble(SnapshotBuilder::sortRent));
        var html = new StringBuilder("<ul>");
        for (var line : lines.stream().limit(MAX_UNITS).toList()) {
            var unit = line.unit();
            var fp = line.floorplan();
            html.append("<li><a href=\"")
                    .append(SeoText.esc(links.unitUrl(unit.id())))
                    .append("\">")
                    .append(SeoText.esc(
                            SeoText.blank(fp.address())
                                    ? "Unit " + unit.id()
                                    : fp.address() + " (unit " + unit.id() + ")"))
                    .append("</a> ")
                    .append(SeoText.esc(fp.name() + " at " + line.propertyName()));
            var facts = new ArrayList<String>();
            var size = bedBath(fp.bedroom(), fp.bathroom());
            if (!size.isEmpty()) facts.add(size);
            if (unit.squareFoot() != null && unit.squareFoot() > 0) facts.add(unit.squareFoot() + " sq ft");
            if (unit.rent() != null && unit.rent() > 0) facts.add(SeoFacts.money(unit.rent()) + " per month");
            if (unit.moveInDate() != null) facts.add("available " + unit.moveInDate());
            if (!facts.isEmpty()) html.append(": ").append(SeoText.esc(String.join(", ", facts)));
            html.append("</li>");
        }
        return html.append("</ul>").toString();
    }

    private String sublets() {
        var html = new StringBuilder("<ul>");
        for (var sublet : data.sublets().stream().limit(MAX_SUBLETS).toList()) {
            html.append("<li><a href=\"")
                    .append(SeoText.esc(links.subletUrl(sublet.assetKey())))
                    .append("\">")
                    .append(SeoText.esc(SeoText.blank(sublet.title()) ? "Sublet" : SeoText.plain(sublet.title())))
                    .append("</a>");
            var facts = new ArrayList<String>();
            if (sublet.bedroom() != null) facts.add(sublet.bedroom() + " bed");
            if (sublet.rent() != null && sublet.rent() > 0) facts.add(SeoFacts.money(sublet.rent()) + " per month");
            if (!SeoText.blank(sublet.address())) facts.add(sublet.address());
            if (sublet.availableFrom() != null) {
                facts.add("available " + sublet.availableFrom()
                        + (sublet.availableTo() == null ? "" : " to " + sublet.availableTo()));
            }
            if (!facts.isEmpty()) html.append(": ").append(SeoText.esc(String.join(", ", facts)));
            html.append("</li>");
        }
        return html.append("</ul>").toString();
    }

    private String jobs() {
        var html = new StringBuilder("<ul>");
        for (var job : data.jobs()) {
            if (job.id() == null) {
                continue;
            }
            html.append("<li><a href=\"")
                    .append(SeoText.esc(links.jobUrl(job.id())))
                    .append("\">")
                    .append(SeoText.esc(SeoText.plain(job.title())))
                    .append("</a>");
            var type = SeoFacts.humanise(job.employmentType());
            if (type != null) html.append(" (").append(SeoText.esc(type)).append(")");
            html.append("</li>");
        }
        return html.append("</ul>").toString();
    }

    private String communities() {
        var html = new StringBuilder("<h2>Our communities</h2><ul>");
        for (var listing : data.listings()) {
            html.append("<li><a href=\"")
                    .append(SeoText.esc(links.propertyUrl(listing.id())))
                    .append("\">")
                    .append(SeoText.esc(listing.name()))
                    .append("</a>");
            if (!listing.floorplans().isEmpty()) {
                html.append(" (").append(listing.floorplans().size()).append(" floorplans)");
            }
            html.append("</li>");
        }
        return html.append("</ul>").toString();
    }
}
