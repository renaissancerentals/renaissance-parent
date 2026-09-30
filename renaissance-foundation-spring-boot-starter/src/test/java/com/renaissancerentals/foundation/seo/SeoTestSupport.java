package com.renaissancerentals.foundation.seo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.time.Clock;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/** Builds a complete SEO stack around an in-memory data source and a fake single page app. */
final class SeoTestSupport {

    static final String HUB = "https://www.renaissancerentals.com";
    static final String HIGH_GROVE = "https://www.highgrovebloomington.com";
    static final String COVENANTER = "https://www.covenanterhill.com";

    private SeoTestSupport() {}

    static final class FakeData implements SeoDataSource {
        final Map<String, SeoData.Property> properties = new HashMap<>();
        final Map<String, SeoData.Floorplan> floorplans = new HashMap<>();
        final Map<String, SeoData.Unit> units = new HashMap<>();
        final List<SeoData.Listing> listings = new ArrayList<>();
        final List<SeoData.Sublet> sublets = new ArrayList<>();
        final List<SeoData.Job> jobs = new ArrayList<>();
        final List<SeoData.Faq> faqs = new ArrayList<>();
        boolean down;

        private void check() {
            if (down) {
                throw new SeoDataUnavailableException("down", null);
            }
        }

        public Optional<SeoData.Property> property(String id) {
            check();
            return Optional.ofNullable(properties.get(id));
        }

        public List<SeoData.Listing> listings() {
            check();
            return listings;
        }

        public Optional<SeoData.Floorplan> floorplan(String id) {
            check();
            return Optional.ofNullable(floorplans.get(id));
        }

        public Optional<SeoData.Unit> unit(String id) {
            check();
            return Optional.ofNullable(units.get(id));
        }

        public List<SeoData.Faq> propertyFaqs(String id) {
            check();
            return faqs;
        }

        public List<SeoData.Faq> floorplanFaqs(String id) {
            check();
            return faqs;
        }

        public List<SeoData.Sublet> sublets() {
            check();
            return sublets;
        }

        public Optional<SeoData.Sublet> sublet(String key) {
            check();
            return sublets.stream().filter(s -> s.assetKey().equals(key)).findFirst();
        }

        public List<SeoData.Job> jobs() {
            check();
            return jobs;
        }

        public Optional<SeoData.Job> job(long id) {
            check();
            return jobs.stream().filter(j -> j.id() == id).findFirst();
        }
    }

    static FakeData data() {
        var data = new FakeData();
        var highGroveRef =
                new SeoData.PropertyRef("high-grove", "High Grove", "1 Grove Rd", "47401", null, "8123332280");
        var covenanterRef =
                new SeoData.PropertyRef("covenanter-hill", "Covenanter Hill", "2 Hill St", "47401", null, null);
        var rockRef = new SeoData.PropertyRef("scholars-rock", "Scholars Rock", "3 Rock Ave", "47401", null, null);

        var unit = new SeoData.Unit(
                "u1",
                750,
                1150f,
                null,
                500f,
                LocalDate.of(2026, 8, 1),
                "1 Grove Rd #4",
                "47401",
                false,
                null,
                null,
                null,
                null,
                null);
        var fpHighGrove = new SeoData.Floorplan(
                "hg-2br",
                "Two Bedroom",
                2,
                1.5f,
                null,
                "1 Grove Rd",
                "47401",
                "Roomy two bedroom",
                null,
                null,
                null,
                highGroveRef,
                List.of(unit),
                List.of(),
                null,
                null,
                null,
                null);
        var fpCovenanter = new SeoData.Floorplan(
                "ch-1br",
                "One Bedroom",
                1,
                1f,
                null,
                "2 Hill St",
                "47401",
                null,
                null,
                null,
                null,
                covenanterRef,
                List.of(),
                List.of(),
                null,
                null,
                null,
                null);
        var fpRock = new SeoData.Floorplan(
                "sr-studio",
                "Studio",
                0,
                1f,
                null,
                "3 Rock Ave",
                "47401",
                null,
                null,
                null,
                null,
                rockRef,
                List.of(),
                List.of(),
                null,
                null,
                null,
                null);
        data.floorplans.put(fpHighGroveKey(), fpHighGrove);
        data.floorplans.put("ch-1br", fpCovenanter);
        data.floorplans.put("sr-studio", fpRock);
        data.units.put(
                "u1",
                new SeoData.Unit(
                        "u1",
                        750,
                        1150f,
                        null,
                        500f,
                        LocalDate.of(2026, 8, 1),
                        "1 Grove Rd #4",
                        "47401",
                        false,
                        null,
                        fpHighGrove,
                        null,
                        null,
                        null));

        data.properties.put(
                "high-grove",
                new SeoData.Property(
                        "high-grove",
                        "High Grove",
                        "1 Grove Rd",
                        "47401",
                        "hg@example.com",
                        "8123332280",
                        "https://facebook.com/hg",
                        null,
                        null,
                        "<p>Contemporary <b>1 &amp; 2</b> bedroom homes.</p>",
                        "High Grove Apartments Bloomington IN",
                        null,
                        List.of(new SeoData.Named("Pool", true, null)),
                        List.of(fpHighGrove),
                        null,
                        null,
                        null));
        data.properties.put(
                "scholars-rock",
                new SeoData.Property(
                        "scholars-rock",
                        "Scholars Rock",
                        "3 Rock Ave",
                        "47401",
                        null,
                        null,
                        null,
                        null,
                        null,
                        "Studios near campus",
                        null,
                        null,
                        List.of(),
                        List.of(fpRock),
                        null,
                        null,
                        null));

        data.listings.add(new SeoData.Listing(
                "high-grove",
                "High Grove",
                List.of(new SeoData.ListingFloorplan(
                        "hg-2br",
                        "Two Bedroom",
                        2,
                        1.5f,
                        "1 Grove Rd",
                        "47401",
                        List.of(new SeoData.ListingUnit("u1", 1150f, 750, java.time.LocalDate.of(2026, 8, 1)))))));
        data.listings.add(new SeoData.Listing(
                "scholars-rock",
                "Scholars Rock",
                List.of(new SeoData.ListingFloorplan(
                        "sr-studio",
                        "Studio",
                        0,
                        1f,
                        "3 Rock Ave",
                        "47401",
                        List.of(new SeoData.ListingUnit("u9", null, null, null))))));
        data.sublets.add(
                new SeoData.Sublet("sub1", "Sunny room", "A room", 1, 600f, "9 Main", "47401", null, null, null, null));
        return data;
    }

    private static String fpHighGroveKey() {
        return "hg-2br";
    }

    static SeoProperties.Organization org() {
        return new SeoProperties.Organization(
                "Renaissance Rentals",
                "8123332280",
                "mail@renaissancerentals.com",
                "3115 S. Sare Rd. Ste 102B",
                null,
                null,
                "47401",
                null,
                "/logos/renaissance-rentals.png",
                List.of());
    }

    static SeoProperties hub(SeoProperties.StaticPage... pages) {
        return new SeoProperties(
                HUB,
                "Renaissance Rentals",
                "Bloomington Apartments | Renaissance Rentals",
                "Apartments & homes for rent in Bloomington.",
                "/logos/renaissance-rentals.png",
                null,
                null,
                null,
                null,
                null,
                null,
                org(),
                List.of(pages));
    }

    static SeoProperties highGrove(SeoProperties.StaticPage... pages) {
        return new SeoProperties(
                HIGH_GROVE,
                "High Grove",
                null,
                "High Grove apartments.",
                "/hero/high-grove.jpg",
                "high-grove",
                null,
                null,
                null,
                null,
                null,
                org(),
                List.of(pages));
    }

    static SeoProperties listingSite(String origin) {
        return new SeoProperties(
                origin,
                "Apartments in Bloomington",
                "Bloomington Apartments Listings",
                "Available apartments in Bloomington.",
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                org(),
                List.of());
    }

    record Stack(MockMvc mvc, SeoService service) {}

    static Stack stack(SeoProperties properties, FakeData data) {
        var mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        var links = new SiteLinks(properties);
        var jsonLd = new SeoJsonLd(properties, links);
        var service = new SeoService(
                properties,
                links,
                List.of(
                        new StaticPageContributor(properties, links, new SnapshotBuilder(links, data)),
                        new EntityPageContributor(properties, links, jsonLd, data),
                        new HomePageContributor(properties, links, jsonLd, new SnapshotBuilder(links, data))),
                List.of(
                        new StaticPageContributor(properties, links, new SnapshotBuilder(links, data)),
                        new EntityPageContributor(properties, links, jsonLd, data),
                        new HomePageContributor(properties, links, jsonLd, new SnapshotBuilder(links, data))),
                Clock.systemUTC());
        var filter = new SeoShellFilter(service, new HeadRenderer(mapper, properties));
        var mvc = MockMvcBuilders.standaloneSetup(new SpaController(), new SeoController(service))
                .addFilters(filter)
                .build();
        return new Stack(mvc, service);
    }

    @RestController
    static class SpaController {
        @GetMapping({
            "/",
            "/floorplans/{id}",
            "/properties/{id}",
            "/units/{id}",
            "/sublets/{id}",
            "/employment/{id}",
            "/contact",
            "/secret",
            "/floorplans",
            "/units",
            "/sublets",
            "/employment"
        })
        ResponseEntity<byte[]> page() throws Exception {
            var body = new ClassPathResource("seo/index.html").getContentAsByteArray();
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .header(HttpHeaders.ETAG, "\"stale\"")
                    .header(HttpHeaders.LAST_MODIFIED, "Wed, 21 Oct 2015 07:28:00 GMT")
                    .body(body);
        }

        @GetMapping("/api/things")
        ResponseEntity<String> api() {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body("{}");
        }

        @GetMapping("/api/assets/{id}/download")
        ResponseEntity<String> asset() {
            return ResponseEntity.ok().body("img");
        }
    }
}
