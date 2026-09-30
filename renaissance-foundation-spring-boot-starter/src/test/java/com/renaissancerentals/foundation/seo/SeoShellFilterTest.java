package com.renaissancerentals.foundation.seo;

import static com.renaissancerentals.foundation.seo.SeoTestSupport.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.util.regex.Pattern;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

class SeoShellFilterTest {

    private static String html(SeoTestSupport.Stack stack, String path) throws Exception {
        return stack.mvc().perform(get(path)).andReturn().getResponse().getContentAsString();
    }

    private static int count(String html, String regex) {
        var m = Pattern.compile(regex).matcher(html);
        var n = 0;
        while (m.find()) n++;
        return n;
    }

    @Test
    void propertySiteHomeGetsPropertyMetadataInTheInitialHtml() throws Exception {
        var stack = stack(highGrove(), data());
        var html = html(stack, "/");

        assertThat(count(html, "<title>")).isEqualTo(1);
        assertThat(html).contains("<title>High Grove Apartments Bloomington IN</title>");
        assertThat(html).doesNotContain("Old static");
        assertThat(count(html, "name=\"description\"")).isEqualTo(1);
        assertThat(html).contains("content=\"Contemporary 1 &amp; 2 bedroom homes.\"");
        assertThat(html).contains("<link rel=\"canonical\" href=\"" + HIGH_GROVE + "/\"/>");
        assertThat(html).contains("index, follow");
        assertThat(html).contains("og:title").contains("twitter:card").contains("og:image");
        assertThat(html).contains("\"@type\":\"ApartmentComplex\"").contains("\"LocationFeatureSpecification\"");
        assertThat(html).contains("<div id=\"root\"><div data-seo-snapshot hidden><h1>High Grove</h1>");
        assertThat(html).contains(HIGH_GROVE + "/floorplans/hg-2br");
    }

    @Test
    void structuredDataIsOneGraphInOneScript() throws Exception {
        var html = html(stack(highGrove(), data()), "/floorplans/hg-2br");
        assertThat(count(html, "application/ld\\+json")).isEqualTo(1);
        assertThat(html).contains("\"@graph\":[");
        assertThat(count(html, "\"@context\"")).isEqualTo(1);
    }

    @Test
    void validatorsAreDroppedBecauseTheBodyIsRewritten() throws Exception {
        var response =
                stack(highGrove(), data()).mvc().perform(get("/")).andReturn().getResponse();
        assertThat(response.getHeader("ETag")).isNull();
        assertThat(response.getHeader("Last-Modified")).isNull();
        assertThat(response.getHeader("Content-Length"))
                .isEqualTo(String.valueOf(response.getContentAsByteArray().length));
    }

    @Test
    void ownFloorplanIsSelfCanonicalWithOffersAndBreadcrumbs() throws Exception {
        var html = html(stack(highGrove(), data()), "/floorplans/hg-2br");
        assertThat(html).contains("<link rel=\"canonical\" href=\"" + HIGH_GROVE + "/floorplans/hg-2br\"/>");
        assertThat(html)
                .contains("\"@type\":\"FloorPlan\"")
                .contains("\"@type\":\"Offer\"")
                .contains("\"BreadcrumbList\"");
        assertThat(html).contains("\"price\":1150.0").contains("\"unitCode\":\"FTK\"");
    }

    @Test
    void floorplanOfAnotherPropertyCanonicalsToItsOwnersSite() throws Exception {
        var html = html(stack(highGrove(), data()), "/floorplans/ch-1br");
        assertThat(html).contains("<link rel=\"canonical\" href=\"" + COVENANTER + "/floorplans/ch-1br\"/>");
    }

    @Test
    void unknownFloorplanIs404AndNoindex() throws Exception {
        var response = stack(highGrove(), data())
                .mvc()
                .perform(get("/floorplans/nope"))
                .andReturn()
                .getResponse();
        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getHeader("X-Robots-Tag")).contains("noindex");
        assertThat(response.getContentAsString())
                .contains("content=\"noindex, follow\"")
                .contains("Page not found");
    }

    @Test
    void hubPointsPropertiesAndFloorplansToTheirOwnSitesButKeepsTheRest() throws Exception {
        var stack = stack(hub(), data());
        assertThat(html(stack, "/properties/high-grove"))
                .contains("<link rel=\"canonical\" href=\"" + HIGH_GROVE + "/\"/>");
        assertThat(html(stack, "/properties/scholars-rock"))
                .contains("<link rel=\"canonical\" href=\"" + HUB + "/properties/scholars-rock\"/>");
        assertThat(html(stack, "/floorplans/hg-2br"))
                .contains("<link rel=\"canonical\" href=\"" + HIGH_GROVE + "/floorplans/hg-2br\"/>");
        assertThat(html(stack, "/floorplans/sr-studio"))
                .contains("<link rel=\"canonical\" href=\"" + HUB + "/floorplans/sr-studio\"/>");
        assertThat(html(stack, "/units/u1")).contains("<link rel=\"canonical\" href=\"" + HUB + "/units/u1\"/>");
    }

    @Test
    void listingSitesCanonicalDetailPagesToTheOwner() throws Exception {
        var stack = stack(listingSite("https://www.apartmentsinbloomington.com"), data());
        assertThat(html(stack, "/floorplans/hg-2br"))
                .contains("<link rel=\"canonical\" href=\"" + HIGH_GROVE + "/floorplans/hg-2br\"/>");
        assertThat(html(stack, "/units/u1")).contains("<link rel=\"canonical\" href=\"" + HUB + "/units/u1\"/>");
        assertThat(html(stack, "/sublets/sub1"))
                .contains("<link rel=\"canonical\" href=\"" + HUB + "/sublets/sub1\"/>");
    }

    @Test
    void hubHomeHasOrganizationMarkup() throws Exception {
        var html = html(stack(hub(), data()), "/");
        assertThat(html)
                .contains("<title>Bloomington Apartments | Renaissance Rentals</title>")
                .contains("\"@type\":\"Organization\"")
                .contains("\"telephone\":\"+1-812-333-2280\"")
                .contains("<link rel=\"canonical\" href=\"" + HUB + "/\"/>");
    }

    @Test
    void routesNobodyDescribedStayOutOfTheIndexButListedOnesAreIndexed() throws Exception {
        var page = new SeoProperties.StaticPage("/contact", "Contact Us", "Call or text.", null, null, null, null);
        var stack = stack(hub(page), data());
        assertThat(html(stack, "/secret")).contains("content=\"noindex, follow\"");
        var contact = html(stack, "/contact");
        assertThat(contact)
                .contains("<title>Contact Us</title>")
                .contains("index, follow")
                .contains("<link rel=\"canonical\" href=\"" + HUB + "/contact\"/>");
    }

    @Test
    void trailingSlashAndQueryStringDoNotChangeTheCanonical() throws Exception {
        var stack = stack(hub(), data());
        var html = stack.mvc()
                .perform(get("/units/u1?utm_source=x"))
                .andReturn()
                .getResponse()
                .getContentAsString();
        assertThat(html).contains("<link rel=\"canonical\" href=\"" + HUB + "/units/u1\"/>");
        assertThat(stack.service().resolve("/units/u1/").canonicalUrl()).isEqualTo(HUB + "/units/u1");
    }

    @Test
    void unavailableDataDegradesToTheGenericPageWithoutFailing() throws Exception {
        var data = data();
        data.down = true;
        var response = stack(highGrove(), data)
                .mvc()
                .perform(get("/floorplans/hg-2br"))
                .andReturn()
                .getResponse();
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsString()).contains("<title>High Grove</title>");
    }

    @Test
    void valuesCannotBreakOutOfTheJsonLdScript() throws Exception {
        var data = data();
        data.properties.put(
                "high-grove",
                new SeoData.Property(
                        "high-grove",
                        "Evil</script><script>alert(1)</script>",
                        "1 Grove Rd",
                        "47401",
                        null,
                        null,
                        null,
                        null,
                        null,
                        "d",
                        "T\"><img src=x>",
                        null,
                        null,
                        null,
                        null,
                        null,
                        null));
        var html = html(stack(highGrove(), data), "/");
        assertThat(html).doesNotContain("</script><script>alert(1)");
        assertThat(html).doesNotContain("<img src=x>");
        assertThat(html).contains("\\u003c/script>");
    }

    @Test
    void jsonApiIsKeptOutOfTheIndexButAssetsAreNot() throws Exception {
        var stack = stack(hub(), data());
        assertThat(stack.mvc()
                        .perform(get("/api/things"))
                        .andReturn()
                        .getResponse()
                        .getHeader("X-Robots-Tag"))
                .isEqualTo("noindex, nofollow");
        assertThat(stack.mvc()
                        .perform(get("/api/assets/abc/download"))
                        .andReturn()
                        .getResponse()
                        .getHeader("X-Robots-Tag"))
                .isNull();
    }

    @Test
    void nonHtmlAndNonGetRequestsPassThroughUntouched() throws Exception {
        MvcResult result =
                stack(hub(), data()).mvc().perform(get("/api/things")).andReturn();
        assertThat(result.getResponse().getContentAsString()).isEqualTo("{}");
    }
}
