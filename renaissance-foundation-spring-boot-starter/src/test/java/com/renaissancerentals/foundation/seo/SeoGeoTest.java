package com.renaissancerentals.foundation.seo;

import static com.renaissancerentals.foundation.seo.SeoTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;

/** The facts an answer engine would quote must be in the initial HTML. */
class SeoGeoTest {

    private static String html(SeoTestSupport.Stack stack, String path) throws Exception {
        return stack.mvc().perform(get(path)).andReturn().getResponse().getContentAsString();
    }

    private static SeoTestSupport.FakeData richData() {
        var data = data();
        var property = new SeoData.PropertyRef("high-grove", "High Grove", "1 Grove Rd", "47401", null, "8123332280");
        var unit = new SeoData.Unit(
                "u1",
                959,
                1784f,
                0f,
                600f,
                LocalDate.of(2026, 8, 1),
                "1 Grove Rd Apt 115, Bloomington, IN",
                "47401",
                false,
                null,
                null,
                "GROUND",
                "Patio",
                "SMALL_DOG_CAT");
        var floorplan = new SeoData.Floorplan(
                "hg-2br",
                "Dorset",
                2,
                1.5f,
                "APARTMENT",
                "1 Grove Rd, Bloomington, IN",
                "47401",
                "Short description",
                null,
                null,
                null,
                property,
                List.of(unit),
                List.of(),
                "LARGE_DOG_SMALL_DOG_CAT",
                "Dogs on ground level only. $25/mo per dog.",
                "Large living space with a walk-through closet.",
                List.of(
                        new SeoData.Utility("water", "INCLUDED_UTILITY", null),
                        new SeoData.Utility("trash removal", "INCLUDED_UTILITY", null),
                        new SeoData.Utility("electric", "RESIDENT_UTILITY", 90f)));
        data.floorplans.put("hg-2br", floorplan);
        data.units.put(
                "u1",
                new SeoData.Unit(
                        "u1",
                        959,
                        1784f,
                        0f,
                        600f,
                        LocalDate.of(2026, 8, 1),
                        "1 Grove Rd Apt 115, Bloomington, IN",
                        "47401",
                        false,
                        null,
                        floorplan,
                        "GROUND",
                        "Patio",
                        "SMALL_DOG_CAT"));
        data.properties.put(
                "high-grove",
                new SeoData.Property(
                        "high-grove",
                        "High Grove",
                        "1 Grove Rd",
                        "47401",
                        "hg@example.com",
                        "8123332280",
                        null,
                        null,
                        null,
                        "Modern homes.",
                        null,
                        null,
                        List.of(
                                new SeoData.Named("Free Parking", false, "AMENITIES"),
                                new SeoData.Named("Package lockers", false, "SERVICES"),
                                new SeoData.Named("Smart thermostat", false, "SMART_LIVING")),
                        List.of(floorplan),
                        "YEARLY",
                        List.of(new SeoData.BusRoute("Bloomington Transit #9", "https://bt.example/9")),
                        new SeoData.LeasingOffice(
                                "Verona Park",
                                "3115 S. Sare Rd. Ste 102B",
                                "47401",
                                "8123332280",
                                "M-F: 9AM-5PM",
                                "Enter from the Sare Road side.")));
        return data;
    }

    @Test
    void propertyPageStatesLeaseOfficeBusRoutesAndGroupsAmenities() throws Exception {
        var html = html(stack(highGrove(), richData()), "/");
        assertThat(html)
                .contains("Yearly leases")
                .contains("Leasing office: 3115 S. Sare Rd. Ste 102B, 47401. Hours: M-F: 9AM-5PM")
                .contains("<a href=\"https://bt.example/9\">Bloomington Transit #9</a>")
                .contains("<h2>Amenities</h2><ul><li>Free Parking</li></ul>")
                .contains("<h2>Services</h2>")
                .contains("<h2>Smart living</h2>");
    }

    @Test
    void floorplanPageStatesPetsUtilitiesAndHighlights() throws Exception {
        var html = html(stack(highGrove(), richData()), "/floorplans/hg-2br");
        assertThat(html)
                .contains("Pets allowed: cats, small dogs and large dogs")
                .contains("Pet policy: Dogs on ground level only. $25/mo per dog.")
                .contains("Rent includes: water and trash removal")
                .contains("Paid by the resident: electric (about $90 per month)")
                .contains("<h2>Highlights</h2><p>Large living space with a walk-through closet.</p>")
                .contains("$600 deposit")
                .contains("ground floor");
    }

    @Test
    void offersCarryPetsAndIncludedUtilitiesAndTheCityIsNotRepeatedInTheStreet() throws Exception {
        var html = html(stack(highGrove(), richData()), "/floorplans/hg-2br");
        assertThat(html)
                .contains("\"petsAllowed\":true")
                .contains("\"name\":\"water included\"")
                .contains("\"streetAddress\":\"1 Grove Rd Apt 115\"")
                .doesNotContain("\"streetAddress\":\"1 Grove Rd Apt 115, Bloomington");
    }

    @Test
    void unitPageUsesTheUnitsOwnPetRule() throws Exception {
        var html = html(stack(hub(), richData()), "/units/u1");
        assertThat(html).contains("Pets allowed: cats and small dogs").contains("Features: Patio");
    }

    @Test
    void listPagesCarryTheListingsForCrawlers() throws Exception {
        var pages = new SeoProperties.StaticPage[] {
            new SeoProperties.StaticPage(
                    "/floorplans", "Homes for Rent", "Browse homes.", null, null, null, "floorplans"),
            new SeoProperties.StaticPage("/units", "Available Now", "Available homes.", null, null, null, "units"),
            new SeoProperties.StaticPage("/sublets", "Sublets", "Sublets.", null, null, null, "sublets"),
            new SeoProperties.StaticPage("/employment", "Jobs", "Jobs.", null, null, null, "jobs")
        };
        var data = richData();
        data.jobs.add(new SeoData.Job(7L, "Leasing Agent", "d", null, "FULL_TIME", null, null, null));
        var stack = stack(hub(pages), data);

        assertThat(html(stack, "/floorplans"))
                .contains("<h1>Homes for Rent</h1>")
                .contains("<a href=\"" + HIGH_GROVE
                        + "/floorplans/hg-2br\">Two Bedroom</a> (2 bed, 1.5 bath, from $1,150 per month, 1 available)");
        assertThat(html(stack, "/units"))
                .contains("<a href=\"" + HUB + "/units/u1\">1 Grove Rd (unit u1)</a>")
                .contains("2 bed, 1.5 bath, 750 sq ft, $1,150 per month, available 2026-08-01");
        assertThat(html(stack, "/sublets"))
                .contains("<a href=\"" + HUB + "/sublets/sub1\">Sunny room</a>")
                .contains("1 bed, $600 per month, 9 Main");
        assertThat(html(stack, "/employment"))
                .contains("<a href=\"" + HUB + "/employment/7\">Leasing Agent</a> (Full time)");
    }

    @Test
    void listPageStillRendersWhenTheDataIsDown() throws Exception {
        var data = richData();
        data.down = true;
        var page =
                new SeoProperties.StaticPage("/units", "Available Now", "Available homes.", null, null, null, "units");
        var html = html(stack(hub(page), data), "/units");
        assertThat(html).contains("<h1>Available Now</h1>").doesNotContain("<ul>");
    }

    @Test
    void hubHomeListsTheCommunities() throws Exception {
        var html = html(stack(hub(), richData()), "/");
        assertThat(html)
                .contains("<h2>Our communities</h2>")
                .contains("<a href=\"" + HIGH_GROVE + "/\">High Grove</a> (1 floorplans)")
                .contains("<a href=\"" + HUB + "/properties/scholars-rock\">Scholars Rock</a>");
    }

    @Test
    void codesBecomeSentences() {
        assertThat(SeoFacts.pets("NO_PET")).isEqualTo("No pets allowed");
        assertThat(SeoFacts.pets("CAT")).isEqualTo("Pets allowed: cats");
        assertThat(SeoFacts.pets(null)).isNull();
        assertThat(SeoFacts.lease("SHORT_TERM")).isEqualTo("Short term leases");
        assertThat(SeoFacts.level("TOP")).isEqualTo("Top floor");
        assertThat(SeoFacts.money(1784f)).isEqualTo("$1,784");
        assertThat(SeoFacts.join(List.of("a", "b", "c"))).isEqualTo("a, b and c");
    }

    @Test
    void indexedPagesGetWebPageAndBreadcrumbMarkup() throws Exception {
        var pages = new SeoProperties.StaticPage[] {
            new SeoProperties.StaticPage("/contact", "Contact Us", "Call us.", null, null, null, null),
            new SeoProperties.StaticPage("/units", "Available Now", "Homes.", null, null, null, "units"),
            new SeoProperties.StaticPage("/secret", "Hidden", "Hidden.", false, null, null, null),
            new SeoProperties.StaticPage("/sublets", "Sublets", "Sublets.", null, null, "/floorplans", null)
        };
        var stack = stack(hub(pages), richData());

        var contact = html(stack, "/contact");
        assertThat(contact)
                .contains("\"@type\":\"WebPage\"")
                .contains("\"@type\":\"BreadcrumbList\"")
                .contains("\"url\":\"" + HUB + "/contact\"")
                .contains("\"inLanguage\":\"en-US\"");
        assertThat(html(stack, "/units")).contains("\"@type\":\"CollectionPage\"");
        // noindex pages and pages whose canonical is elsewhere carry no page markup
        assertThat(html(stack, "/secret")).doesNotContain("application/ld+json");
        assertThat(html(stack, "/sublets")).doesNotContain("\"@type\":\"WebPage\"");
    }
}
