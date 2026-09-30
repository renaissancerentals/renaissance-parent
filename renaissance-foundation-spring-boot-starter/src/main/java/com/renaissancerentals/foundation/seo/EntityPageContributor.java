package com.renaissancerentals.foundation.seo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Pages backed by listing data: property, floorplan, unit, sublet and job pages. Missing records answer 404, and
 * every page canonicals to the site that owns it (see {@link SiteLinks}).
 */
public class EntityPageContributor implements SeoPageContributor, SitemapContributor {

    private static final Logger log = LoggerFactory.getLogger(EntityPageContributor.class);
    private static final Pattern DRIVE_ID = Pattern.compile("[?&]id=([^&]+)");
    private static final Pattern PROPERTY = Pattern.compile("^/properties/([^/]+)$");
    private static final Pattern FLOORPLAN = Pattern.compile("^/floorplans/([^/]+)$");
    private static final Pattern UNIT = Pattern.compile("^/units/([^/]+)$");
    private static final Pattern SUBLET = Pattern.compile("^/sublets/([^/]+)$");
    private static final Pattern JOB = Pattern.compile("^/employment/(\\d+)$");

    private final SeoProperties properties;
    private final SiteLinks links;
    private final SeoJsonLd jsonLd;
    private final SeoDataSource data;

    public EntityPageContributor(SeoProperties properties, SiteLinks links, SeoJsonLd jsonLd, SeoDataSource data) {
        this.properties = properties;
        this.links = links;
        this.jsonLd = jsonLd;
        this.data = data;
    }

    private String owned() {
        return properties.ownedPropertyId();
    }

    private boolean isHub() {
        return links.isSelf(links.hubOrigin());
    }

    // ---------------------------------------------------------------- pages

    @Override
    public Optional<PageMeta> resolve(String path) {
        try {
            if (!SeoText.blank(owned()) && "/".equals(path)) {
                return Optional.of(propertyPage(owned(), path));
            }
            Matcher m;
            if ((m = PROPERTY.matcher(path)).matches()) {
                return Optional.of(propertyPage(m.group(1), path));
            }
            if ((m = FLOORPLAN.matcher(path)).matches()) {
                return Optional.of(floorplanPage(m.group(1), path));
            }
            if ((m = UNIT.matcher(path)).matches()) {
                return Optional.of(unitPage(m.group(1), path));
            }
            if ((m = SUBLET.matcher(path)).matches()) {
                return Optional.of(subletPage(m.group(1), path));
            }
            if ((m = JOB.matcher(path)).matches()) {
                return Optional.of(jobPage(Long.parseLong(m.group(1)), path));
            }
            return Optional.empty();
        } catch (SeoDataUnavailableException e) {
            log.warn("SEO data unavailable for {}: {}", path, e.getMessage());
            return Optional.of(generic(path));
        }
    }

    private PageMeta generic(String path) {
        var title = SeoText.blank(properties.defaultTitle()) ? properties.siteName() : properties.defaultTitle();
        return new PageMeta(
                SeoText.plain(title),
                SeoText.description(properties.defaultDescription(), properties.siteName()),
                links.self(path),
                true,
                200,
                "website",
                links.absolute(properties.defaultImage()),
                List.of(),
                null);
    }

    private PageMeta notFound(String path) {
        return new PageMeta(
                "Page not found | " + properties.siteName(),
                SeoText.description(properties.defaultDescription(), properties.siteName()),
                links.self(path),
                false,
                404,
                "website",
                links.absolute(properties.defaultImage()),
                List.of(),
                null);
    }

    private PageMeta propertyPage(String propertyId, String path) {
        var found = data.property(propertyId);
        if (found.isEmpty()) {
            return notFound(path);
        }
        var property = found.get();
        var url = links.propertyUrl(propertyId);
        var generated = property.name() + " | Apartments for Rent in "
                + properties.organization().locality() + ", "
                + properties.organization().region();
        var title = SeoText.title(property.htmlTitle(), generated);
        var description = SeoText.description(
                property.metaDescription(),
                SeoText.blank(property.description())
                        ? property.name() + " apartments for rent in "
                                + properties.organization().locality() + ", "
                                + properties.organization().region() + ". Contact us to schedule a tour."
                        : property.description());
        var image = image(property.coverImage());

        var ld = new ArrayList<Map<String, Object>>();
        ld.add(jsonLd.apartmentComplex(property, url, description, image));
        if (!links.propertyUrl(propertyId).equals(links.self("/"))) {
            ld.add(jsonLd.breadcrumbs(List.of(
                    new String[] {properties.siteName(), links.self("/")}, new String[] {property.name(), url})));
        }
        faq(() -> data.propertyFaqs(propertyId), ld);

        var html = new StringBuilder(
                "<h1>" + SeoText.esc(property.name()) + "</h1><p>" + SeoText.esc(description) + "</p>");
        html.append(contact(property.address(), property.zipcode(), property.phone(), property.email()));
        appendPropertyFacts(html, property);
        appendAmenities(html, property.amenities());
        if (property.floorplans() != null && !property.floorplans().isEmpty()) {
            html.append("<h2>Floorplans</h2><ul>");
            for (var fp : property.floorplans()) {
                html.append("<li><a href=\"")
                        .append(SeoText.esc(links.floorplanUrl(propertyId, fp.id())))
                        .append("\">")
                        .append(SeoText.esc(fp.name()))
                        .append("</a>")
                        .append(bedBath(fp))
                        .append("</li>");
            }
            html.append("</ul>");
        }
        return new PageMeta(title, description, url, true, 200, "website", image, ld, html.toString());
    }

    private PageMeta floorplanPage(String floorplanId, String path) {
        var found = data.floorplan(floorplanId);
        if (found.isEmpty()) {
            return notFound(path);
        }
        var fp = found.get();
        var propertyId = fp.property() == null ? null : fp.property().id();
        var propertyName =
                fp.property() == null ? properties.siteName() : fp.property().name();
        var url = links.floorplanUrl(propertyId, floorplanId);
        var generated = fp.name() + " | " + propertyName + ", "
                + properties.organization().locality() + " "
                + properties.organization().region();
        var title = SeoText.title(fp.htmlTitle(), generated);
        var description = SeoText.description(
                fp.metaDescription(),
                SeoText.blank(fp.description())
                        ? fp.name() + " at " + propertyName + " in "
                                + properties.organization().locality() + ", "
                                + properties.organization().region() + bedBathPlain(fp) + ". See rent and availability."
                        : fp.description());
        var image = image(fp.coverImage());

        var ld = new ArrayList<Map<String, Object>>();
        ld.add(jsonLd.breadcrumbs(trail(propertyId, propertyName, fp.name(), url)));
        ld.add(jsonLd.floorPlan(fp, url, description, image));
        ld.addAll(jsonLd.offers(fp, fp.units(), url));
        faq(() -> data.floorplanFaqs(floorplanId), ld);

        var html = new StringBuilder("<h1>" + SeoText.esc(fp.name()) + "</h1><p>" + SeoText.esc(description) + "</p>");
        html.append("<p>")
                .append(SeoText.esc(propertyName))
                .append(SeoText.esc(bedBathPlain(fp)))
                .append("</p>");
        html.append(contact(
                fp.address(),
                fp.zipcode(),
                fp.property() == null ? null : fp.property().phone(),
                null));
        appendFloorplanFacts(html, fp, description);
        appendUnits(html, fp.units());
        return new PageMeta(title, description, url, true, 200, "website", image, ld, html.toString());
    }

    private PageMeta unitPage(String unitId, String path) {
        var found = data.unit(unitId);
        if (found.isEmpty() || found.get().floorplan() == null) {
            return notFound(path);
        }
        var unit = found.get();
        var fp = unit.floorplan();
        var propertyId = fp.property() == null ? null : fp.property().id();
        var propertyName =
                fp.property() == null ? properties.siteName() : fp.property().name();
        var url = links.unitUrl(unitId);
        var address = SeoText.blank(unit.address()) ? fp.address() : unit.address();
        var label = SeoText.blank(address) ? fp.name() : address;
        var price = SeoJsonLd.lowestRent(unit);
        var generated = label + " | " + fp.name() + " for rent in "
                + properties.organization().locality();
        var title = SeoText.title(null, generated);
        var description = SeoText.description(
                null,
                fp.name() + " at " + propertyName + (SeoText.blank(address) ? "" : ", " + address)
                        + bedBathPlain(fp) + (price == null ? "" : ", from " + SeoFacts.money(price) + " per month")
                        + ". Check availability and schedule a tour.");
        var image = image(SeoText.blank(unit.coverImage()) ? fp.coverImage() : unit.coverImage());

        var ld = new ArrayList<Map<String, Object>>();
        ld.add(jsonLd.breadcrumbs(trail(propertyId, propertyName, label, url)));
        ld.add(jsonLd.floorPlan(fp, url, description, image));
        ld.addAll(jsonLd.offers(fp, List.of(unit), url));

        var html = new StringBuilder("<h1>" + SeoText.esc(label) + "</h1><p>" + SeoText.esc(description) + "</p>");
        html.append(contact(address, SeoText.blank(unit.zipcode()) ? fp.zipcode() : unit.zipcode(), null, null));
        appendFloorplanFacts(html, withUnitPets(fp, unit), description);
        if (!SeoText.blank(unit.features())) {
            html.append("<p>Features: ")
                    .append(SeoText.esc(SeoText.plain(unit.features())))
                    .append("</p>");
        }
        appendUnits(html, List.of(unit));
        return new PageMeta(title, description, url, true, 200, "website", image, ld, html.toString());
    }

    private PageMeta subletPage(String assetKey, String path) {
        var found = data.sublet(assetKey);
        if (found.isEmpty()) {
            return notFound(path);
        }
        var sublet = found.get();
        var url = links.subletUrl(assetKey);
        var generated = (sublet.bedroom() == null ? "" : sublet.bedroom() + " bedroom ") + "sublet"
                + (SeoText.blank(sublet.address()) ? "" : " at " + sublet.address()) + " | " + properties.siteName();
        var title = SeoText.title(sublet.title() == null ? null : sublet.title() + " | Sublet", generated);
        var description = SeoText.description(sublet.description(), generated);
        var html = new StringBuilder(
                "<h1>" + SeoText.esc(SeoText.plain(sublet.title())) + "</h1><p>" + SeoText.esc(description) + "</p>");
        html.append(contact(sublet.address(), sublet.zipcode(), null, null));
        return new PageMeta(
                title, description, url, true, 200, "website", image(sublet.coverImage()), List.of(), html.toString());
    }

    private PageMeta jobPage(long id, String path) {
        var found = data.job(id);
        if (found.isEmpty()) {
            return notFound(path);
        }
        var job = found.get();
        var url = links.jobUrl(id);
        var title = SeoText.title(
                "Now Hiring: " + SeoText.plain(job.title()) + " | " + properties.siteName(),
                "Now Hiring | " + properties.siteName());
        var description = SeoText.description(job.description(), "Join the team at " + properties.siteName() + ".");
        var html = new StringBuilder("<h1>" + SeoText.esc("Now Hiring: " + SeoText.plain(job.title())) + "</h1><p>"
                + SeoText.esc(description) + "</p>");
        return new PageMeta(
                title,
                description,
                url,
                true,
                200,
                "website",
                links.absolute(properties.defaultImage()),
                List.of(jsonLd.jobPosting(job, url)),
                html.toString());
    }

    // ---------------------------------------------------------------- helpers

    private List<String[]> trail(String propertyId, String propertyName, String leafName, String leafUrl) {
        var trail = new ArrayList<String[]>();
        trail.add(new String[] {properties.siteName(), links.self("/")});
        if (propertyId != null) {
            trail.add(new String[] {propertyName, links.propertyUrl(propertyId)});
        }
        trail.add(new String[] {leafName, leafUrl});
        return trail;
    }

    private void faq(java.util.function.Supplier<List<SeoData.Faq>> source, List<Map<String, Object>> ld) {
        try {
            var page = jsonLd.faqPage(source.get());
            if (page != null) {
                ld.add(page);
            }
        } catch (SeoDataUnavailableException e) {
            log.debug("FAQ data unavailable: {}", e.getMessage());
        }
    }

    private static String bedBathPlain(SeoData.Floorplan fp) {
        var out = new StringBuilder();
        if (fp.bedroom() != null) {
            out.append(fp.bedroom() == 0 ? ", studio" : ", " + fp.bedroom() + " bed");
        }
        if (fp.bathroom() != null) {
            out.append(", ").append(trim(fp.bathroom())).append(" bath");
        }
        return out.toString();
    }

    private static String bedBath(SeoData.Floorplan fp) {
        var text = bedBathPlain(fp);
        return text.isEmpty() ? "" : " (" + SeoText.esc(text.substring(2)) + ")";
    }

    private static String trim(Float value) {
        return value == Math.rint(value) ? String.valueOf(value.intValue()) : String.valueOf(value);
    }

    private static String contact(String address, String zipcode, String phone, String email) {
        var parts = new ArrayList<String>();
        if (!SeoText.blank(address)) {
            parts.add(SeoText.esc(address + (SeoText.blank(zipcode) ? "" : ", " + zipcode)));
        }
        if (!SeoText.blank(phone)) {
            parts.add(SeoText.esc(SeoText.phone(phone)));
        }
        if (!SeoText.blank(email)) {
            parts.add(SeoText.esc(email));
        }
        return parts.isEmpty() ? "" : "<p>" + String.join(" · ", parts) + "</p>";
    }

    private void appendPropertyFacts(StringBuilder html, SeoData.Property property) {
        var facts = new ArrayList<String>();
        var lease = SeoFacts.lease(property.leaseType());
        if (lease != null) {
            facts.add(lease);
        }
        if (!property.floorplans().isEmpty()) {
            // pet rules are set per floorplan and unit, never for the whole property
            facts.add("Pet policies vary by floorplan; see each floorplan for what is allowed.");
        }
        var office = property.leasingOffice();
        if (office != null && !SeoText.blank(office.address())) {
            var text = "Leasing office: " + office.address()
                    + (SeoText.blank(office.zipcode()) ? "" : ", " + office.zipcode());
            if (!SeoText.blank(office.officeHours())) {
                text += ". Hours: " + SeoText.plain(office.officeHours());
            }
            facts.add(text);
            if (!SeoText.blank(office.direction())) {
                facts.add(SeoText.clip(office.direction(), 300));
            }
        }
        if (!facts.isEmpty()) {
            html.append("<h2>Quick facts</h2><ul>");
            facts.forEach(f -> html.append("<li>").append(SeoText.esc(f)).append("</li>"));
            html.append("</ul>");
        }
        var routes = property.busRoutes().stream()
                .filter(r -> !SeoText.blank(r.busRoute()))
                .toList();
        if (!routes.isEmpty()) {
            html.append("<h2>Bus routes</h2><ul>");
            for (var route : routes) {
                html.append("<li>");
                if (SeoText.blank(route.busRouteLink())) {
                    html.append(SeoText.esc(route.busRoute()));
                } else {
                    html.append("<a href=\"")
                            .append(SeoText.esc(route.busRouteLink()))
                            .append("\">")
                            .append(SeoText.esc(route.busRoute()))
                            .append("</a>");
                }
                html.append("</li>");
            }
            html.append("</ul>");
        }
    }

    /** A unit can override the floorplan's pet rule. */
    private static SeoData.Floorplan withUnitPets(SeoData.Floorplan fp, SeoData.Unit unit) {
        if (SeoText.blank(unit.allowedPet())) {
            return fp;
        }
        return new SeoData.Floorplan(
                fp.id(),
                fp.name(),
                fp.bedroom(),
                fp.bathroom(),
                fp.style(),
                fp.address(),
                fp.zipcode(),
                fp.description(),
                fp.htmlTitle(),
                fp.metaDescription(),
                fp.coverImage(),
                fp.property(),
                fp.units(),
                fp.amenities(),
                unit.allowedPet(),
                fp.petPolicy(),
                fp.highlights(),
                fp.utilities());
    }

    private static void appendAmenities(StringBuilder html, List<SeoData.Named> amenities) {
        if (amenities == null || amenities.isEmpty()) {
            return;
        }
        var groups = new java.util.LinkedHashMap<String, List<String>>();
        amenities.stream().filter(a -> !SeoText.blank(a.name())).limit(60).forEach(a -> groups.computeIfAbsent(
                        SeoFacts.heading(a.type()), k -> new ArrayList<>())
                .add(SeoText.plain(a.name())));
        groups.forEach((heading, names) -> {
            html.append("<h2>").append(SeoText.esc(heading)).append("</h2><ul>");
            names.forEach(n -> html.append("<li>").append(SeoText.esc(n)).append("</li>"));
            html.append("</ul>");
        });
    }

    private static void appendFloorplanFacts(StringBuilder html, SeoData.Floorplan fp, String description) {
        var facts = new ArrayList<String>();
        var pets = SeoFacts.pets(fp.allowedPet());
        if (pets != null) {
            facts.add(pets);
        }
        if (!SeoText.blank(fp.petPolicy())) {
            facts.add("Pet policy: " + SeoText.plain(fp.petPolicy()));
        }
        var included = SeoFacts.includedUtilities(fp.utilities());
        if (!included.isEmpty()) {
            facts.add("Rent includes: " + SeoFacts.join(included));
        }
        var resident = SeoFacts.residentUtilities(fp.utilities());
        if (!resident.isEmpty()) {
            facts.add("Paid by the resident: " + SeoFacts.join(resident));
        }
        if (!facts.isEmpty()) {
            html.append("<h2>Quick facts</h2><ul>");
            facts.forEach(f -> html.append("<li>").append(SeoText.esc(f)).append("</li>"));
            html.append("</ul>");
        }
        var highlights = SeoText.plain(fp.highlights());
        if (!highlights.isEmpty() && !highlights.equals(SeoText.plain(fp.description()))) {
            html.append("<h2>Highlights</h2><p>")
                    .append(SeoText.esc(highlights))
                    .append("</p>");
        } else if (!SeoText.plain(fp.description()).isEmpty()
                && !SeoText.plain(fp.description()).equals(SeoText.plain(description))) {
            html.append("<p>")
                    .append(SeoText.esc(SeoText.plain(fp.description())))
                    .append("</p>");
        }
    }

    private static void appendUnits(StringBuilder html, List<SeoData.Unit> units) {
        if (units == null || units.isEmpty()) {
            return;
        }
        html.append("<h2>Available homes</h2><ul>");
        for (var unit : units) {
            var price = SeoJsonLd.lowestRent(unit);
            html.append("<li>");
            html.append(SeoText.esc(SeoText.blank(unit.address()) ? "Unit " + unit.id() : unit.address()));
            if (unit.squareFoot() != null && unit.squareFoot() > 0) {
                html.append(", ").append(unit.squareFoot()).append(" sq ft");
            }
            if (price != null) {
                html.append(", ").append(SeoFacts.money(price)).append(" per month");
            }
            if (unit.moveInDate() != null) {
                html.append(", available ").append(unit.moveInDate());
            }
            if (unit.deposit() != null && unit.deposit() > 0) {
                html.append(", ").append(SeoFacts.money(unit.deposit())).append(" deposit");
            }
            var level = SeoFacts.level(unit.level());
            if (level != null) {
                html.append(", ").append(SeoText.esc(level.toLowerCase(java.util.Locale.ROOT)));
            }
            if (Boolean.TRUE.equals(unit.furnished())) {
                html.append(", furnished");
            }
            html.append("</li>");
        }
        html.append("</ul>");
    }

    private String image(String raw) {
        if (SeoText.blank(raw)) {
            return links.absolute(properties.defaultImage());
        }
        if (raw.contains("drive.google.com")) {
            var m = DRIVE_ID.matcher(raw);
            return m.find()
                    ? properties.dataBaseUrls().get(0) + "/api/assets/"
                            + m.group(1).trim() + "/download"
                    : links.absolute(properties.defaultImage());
        }
        return links.absolute(raw);
    }

    // ---------------------------------------------------------------- sitemap

    @Override
    public List<SitemapEntry> entries() {
        try {
            if (!SeoText.blank(owned())) {
                return ownedPropertyEntries();
            }
            return isHub() ? hubEntries() : List.of();
        } catch (SeoDataUnavailableException e) {
            log.warn("SEO sitemap data unavailable: {}", e.getMessage());
            return List.of();
        }
    }

    private List<SitemapEntry> ownedPropertyEntries() {
        var entries = new ArrayList<SitemapEntry>();
        var property = data.property(owned()).orElse(null);
        if (property == null) {
            return entries;
        }
        entries.add(new SitemapEntry(links.propertyUrl(owned()), null, "weekly", 1.0, property.name()));
        var shown = new ArrayList<SeoData.Property>(List.of(property));
        for (var extraId : properties.additionalPropertyIds()) {
            data.property(extraId).ifPresent(shown::add);
        }
        for (var shownProperty : shown) {
            if (shownProperty.floorplans() == null) {
                continue;
            }
            for (var fp : shownProperty.floorplans()) {
                var url = links.floorplanUrl(shownProperty.id(), fp.id());
                if (links.isSelf(url)) {
                    entries.add(new SitemapEntry(url, null, "weekly", 0.8, fp.name() + " at " + shownProperty.name()));
                }
            }
        }
        return entries;
    }

    private List<SitemapEntry> hubEntries() {
        var entries = new ArrayList<SitemapEntry>();
        for (var listing : data.listings()) {
            var propertyUrl = links.propertyUrl(listing.id());
            if (links.isSelf(propertyUrl)) {
                entries.add(new SitemapEntry(propertyUrl, null, "weekly", 0.9, listing.name()));
            }
            if (listing.floorplans() == null) {
                continue;
            }
            for (var fp : listing.floorplans()) {
                var url = links.floorplanUrl(listing.id(), fp.id());
                if (links.isSelf(url)) {
                    entries.add(new SitemapEntry(url, null, "weekly", 0.8, fp.name() + " at " + listing.name()));
                }
                if (fp.units() == null) {
                    continue;
                }
                for (var unit : fp.units()) {
                    entries.add(new SitemapEntry(links.unitUrl(unit.id()), null, "daily", 0.6, null));
                }
            }
        }
        for (var sublet : data.sublets()) {
            LocalDate created =
                    sublet.createdDate() == null ? null : sublet.createdDate().toLocalDate();
            entries.add(new SitemapEntry(links.subletUrl(sublet.assetKey()), created, "weekly", 0.4, sublet.title()));
        }
        for (var job : data.jobs()) {
            if (job.id() != null) {
                entries.add(new SitemapEntry(links.jobUrl(job.id()), job.datePosted(), "weekly", 0.4, job.title()));
            }
        }
        return entries;
    }
}
