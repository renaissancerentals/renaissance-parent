package com.renaissancerentals.foundation.seo;

import static com.renaissancerentals.foundation.seo.SeoTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.List;
import org.junit.jupiter.api.Test;

class SeoEndpointsTest {

    private static String body(SeoTestSupport.Stack stack, String path) throws Exception {
        return stack.mvc().perform(get(path)).andReturn().getResponse().getContentAsString();
    }

    @Test
    void hubSitemapListsOnlyWhatTheHubOwns() throws Exception {
        var page = new SeoProperties.StaticPage("/contact", "Contact Us", null, null, null, null, null);
        var xml = body(stack(hub(page), data()), "/sitemap.xml");

        assertThat(xml)
                .contains("<loc>" + HUB + "/</loc>")
                .contains("<loc>" + HUB + "/contact</loc>")
                .contains("<loc>" + HUB + "/properties/scholars-rock</loc>")
                .contains("<loc>" + HUB + "/floorplans/sr-studio</loc>")
                .contains("<loc>" + HUB + "/units/u1</loc>")
                .contains("<loc>" + HUB + "/sublets/sub1</loc>");
        // owned by the property's own site
        assertThat(xml).doesNotContain("/properties/high-grove").doesNotContain("/floorplans/hg-2br");
    }

    @Test
    void propertySiteSitemapListsItsHomeAndFloorplans() throws Exception {
        var xml = body(stack(highGrove(), data()), "/sitemap.xml");
        assertThat(xml)
                .contains("<loc>" + HIGH_GROVE + "/</loc>")
                .contains("<loc>" + HIGH_GROVE + "/floorplans/hg-2br</loc>")
                .doesNotContain("scholars-rock");
    }

    @Test
    void listingSiteSitemapOnlyHasItsHomePage() throws Exception {
        var xml = body(stack(listingSite("https://www.apartmentsinbloomington.com"), data()), "/sitemap.xml");
        assertThat(xml)
                .contains("<loc>https://www.apartmentsinbloomington.com/</loc>")
                .doesNotContain("/floorplans/")
                .doesNotContain("/units/");
    }

    @Test
    void sitemapSurvivesADataOutage() throws Exception {
        var data = data();
        data.down = true;
        var xml = body(stack(hub(), data), "/sitemap.xml");
        assertThat(xml).contains("<loc>" + HUB + "/</loc>");
    }

    @Test
    void robotsPointsAtTheSitemapAndDoesNotBlockTheApi() throws Exception {
        var robots = body(stack(highGrove(), data()), "/robots.txt");
        assertThat(robots)
                .contains("Allow: /")
                .contains("Sitemap: " + HIGH_GROVE + "/sitemap.xml")
                .doesNotContain("Disallow: /api");
    }

    @Test
    void llmsTxtDescribesTheSite() throws Exception {
        var llms = body(stack(hub(), data()), "/llms.txt");
        assertThat(llms)
                .startsWith("# Renaissance Rentals")
                .contains("> Apartments & homes for rent in Bloomington.")
                .contains("- Phone: +1-812-333-2280")
                .contains("](" + HUB + "/properties/scholars-rock)");
    }

    @Test
    void additionalPropertiesAreListedOnTheSiteThatShowsThem() throws Exception {
        var data = data();
        var shortTerm = new SeoData.Floorplan(
                "st-1br",
                "1 Bedroom Flat",
                1,
                1f,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                List.of(),
                List.of(),
                null,
                null,
                null,
                null);
        data.properties.put(
                "high-grove-short-term",
                new SeoData.Property(
                        "high-grove-short-term",
                        "High Grove Short Term",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        List.of(),
                        List.of(shortTerm),
                        null,
                        null,
                        null));
        var base = highGrove();
        var props = new SeoProperties(
                base.canonicalOrigin(),
                base.siteName(),
                null,
                null,
                null,
                "high-grove",
                List.of("high-grove-short-term"),
                null,
                null,
                java.util.Map.of("high-grove", HIGH_GROVE, "high-grove-short-term", HIGH_GROVE),
                null,
                org(),
                List.of());
        var xml = body(stack(props, data), "/sitemap.xml");
        assertThat(xml).contains("<loc>" + HIGH_GROVE + "/floorplans/st-1br</loc>");
    }
}
