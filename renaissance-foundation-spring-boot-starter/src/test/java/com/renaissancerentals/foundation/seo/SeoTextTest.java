package com.renaissancerentals.foundation.seo;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SeoTextTest {

    @Test
    void plainStripsMarkupAndEntities() {
        assertThat(SeoText.plain("<p>Two&nbsp;<b>big</b> &amp; bright</p>\n rooms"))
                .isEqualTo("Two big & bright rooms");
        assertThat(SeoText.plain(null)).isEmpty();
    }

    @Test
    void clipCutsOnAWordBoundary() {
        var clipped = SeoText.clip("word ".repeat(60), 160);
        assertThat(clipped.length()).isLessThanOrEqualTo(160);
        assertThat(clipped).endsWith("word…");
    }

    @Test
    void storedTitleIsKeptOnlyWhenItFitsTheSearchResult() {
        assertThat(SeoText.title("Nice Title For A Page", "generated")).isEqualTo("Nice Title For A Page");
        assertThat(SeoText.title("x".repeat(60), "generated")).hasSize(60);
        assertThat(SeoText.title("x".repeat(61), "generated")).isEqualTo("generated");
        assertThat(SeoText.title("Verona Park", "generated")).isEqualTo("generated");
        assertThat(SeoText.title(null, "generated")).isEqualTo("generated");
        assertThat(SeoText.title(null, "word ".repeat(30)).length()).isLessThanOrEqualTo(60);
    }

    @Test
    void phoneNumbersAreNormalised() {
        assertThat(SeoText.phone("8123332280")).isEqualTo("+1-812-333-2280");
        assertThat(SeoText.phone("(812) 333-2280")).isEqualTo("+1-812-333-2280");
        assertThat(SeoText.phone(" ")).isNull();
    }

    @Test
    void canonicalPathsAreNormalised() {
        assertThat(SiteLinks.normalise("/a//b/")).isEqualTo("/a/b");
        assertThat(SiteLinks.normalise("")).isEqualTo("/");
        assertThat(SiteLinks.normalise("/")).isEqualTo("/");
    }
}
