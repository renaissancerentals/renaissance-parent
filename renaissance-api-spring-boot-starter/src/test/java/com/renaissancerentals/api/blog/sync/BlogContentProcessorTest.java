package com.renaissancerentals.api.blog.sync;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;
import org.junit.jupiter.api.Test;

class BlogContentProcessorTest {

    private final BlogContentProcessor processor = new BlogContentProcessor();

    @Test
    void scriptsAndEventHandlersNeverSurvive() {
        var out = processor.process(
                "<p onclick=\"x()\">Hi</p><script>alert(1)</script><iframe src=\"https://evil\"></iframe>");

        assertThat(out)
                .contains("<p>Hi</p>")
                .doesNotContain("script")
                .doesNotContain("onclick")
                .doesNotContain("iframe");
    }

    @Test
    void javascriptLinksAreDropped() {
        var out = processor.process("<a href=\"javascript:alert(1)\">x</a>");

        assertThat(out).doesNotContain("javascript");
    }

    @Test
    void aLinkToAnotherNoteBecomesALinkToThatPostOnThisSite() {
        var out = processor.process("<a data-title-link-src=\"true\" href=\"/ab/abcd\">Other post</a>");

        assertThat(out).contains("href=\"/blogs/abcd\"").contains("data-post=\"abcd\"");
        assertThat(processor.linkedPosts(out)).containsExactly("abcd");
    }

    @Test
    void aLinkWithNoUsableTargetBecomesPlainText() {
        var out = processor.process("<a data-title-link-src=\"true\" href=\"/\">Nowhere</a>");

        assertThat(out).contains("Nowhere").doesNotContain("<a");
    }

    @Test
    void anImagePointsAtTheCopyThisSiteKeeps() {
        var out = processor.process(
                "<figure data-media-image-src=\"ab/xyzw\"><img src=\"https://notes.example.com/media/ab/xyzw/content\" alt=\"A\"/></figure>");

        assertThat(out).contains("src=\"/api/blog/media/ab/xyzw\"").doesNotContain("notes.example.com");
    }

    @Test
    void anImageWithAnOddReferenceIsRemovedRatherThanLoadedFromElsewhere() {
        var out = processor.process(
                "<div data-media-image-src=\"../etc/passwd\"><img src=\"https://evil/x.png\"/></div>");

        assertThat(out).doesNotContain("evil").doesNotContain("img");
    }

    @Test
    void aDocumentLinkPointsAtTheCopyToo() {
        var out = processor.process(
                "<div data-media-document-src=\"ab/doc1\"><a href=\"https://notes/x\">Lease</a></div>");

        assertThat(out).contains("href=\"/api/blog/media/ab/doc1\"");
    }

    @Test
    void outsideLinksKeepOnlyOrdinarySchemesAndOpenSafely() {
        var out = processor.process(
                "<a href=\"https://example.com\">e</a><a href=\"mailto:a@b.com\">m</a><a href=\"ftp://x\">f</a>");

        assertThat(out).contains("href=\"https://example.com\"").contains("rel=\"noopener noreferrer\"");
        assertThat(out).contains("mailto:a@b.com").doesNotContain("ftp:");
    }

    @Test
    void emptyContentIsEmpty() {
        assertThat(processor.process(null)).isEmpty();
        assertThat(processor.process("  ")).isEmpty();
    }

    @Test
    void atReadTimeALinkToAPostThatIsNotHereIsPlainTextAndTheHelperNeverLeaves() {
        var stored = processor.process(
                "<a data-title-link-src=\"true\" href=\"/ab/abcd\">Here</a> and <a data-title-link-src=\"true\" href=\"/ab/wxyz\">gone</a>");

        var out = processor.resolveLinks(stored, Set.of("abcd"));

        assertThat(out)
                .contains("<a href=\"/blogs/abcd\">Here</a>")
                .contains("and gone")
                .doesNotContain("wxyz");
        assertThat(out).doesNotContain("data-post");
    }
}
