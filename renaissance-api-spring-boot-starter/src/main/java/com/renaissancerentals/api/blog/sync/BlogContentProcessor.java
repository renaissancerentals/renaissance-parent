package com.renaissancerentals.api.blog.sync;

import java.util.Set;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

/**
 * Turns the notes platform's rendered content into what this site stores and shows. The content is never trusted: it is
 * sanitized here with this site's own policy, whatever the sender already did.
 *
 * <ul>
 *   <li>A link to another note becomes a link to that post on this site ({@code /blogs/<id>}); at read time one whose post is
 *       not here is turned into plain text.
 *   <li>An image or document points at the copy this site keeps.
 *   <li>Every other link is kept only if it is an ordinary web or mail link, and opens safely.
 * </ul>
 */
@Component
public class BlogContentProcessor {

    public static final String POST_ATTRIBUTE = "data-post";
    private static final String BASE = "https://blog.invalid/";
    public static final String MEDIA_PATH = "/api/blog/media/";

    private static final Safelist SAFELIST = Safelist.relaxed()
            .addTags("figure", "figcaption", "hr", "section", "details", "summary")
            .addAttributes(":all", "class", "id")
            .addAttributes("a", POST_ATTRIBUTE, "rel")
            .addAttributes("img", "loading")
            .removeProtocols("a", "href", "ftp")
            .preserveRelativeLinks(true);

    /** What to store for a post's content. */
    public String process(String html) {
        if (html == null || html.isBlank()) return "";
        Document doc = Jsoup.parseBodyFragment(html);

        for (Element link : doc.select("a[data-title-link-src]")) {
            String nid = lastSegment(link.attr("href"));
            link.removeAttr("data-title-link-src");
            if (nid.isEmpty()) {
                link.unwrap();
            } else {
                link.attr(POST_ATTRIBUTE, nid);
                link.attr("href", "/blogs/" + nid);
            }
        }
        for (Element marker : doc.select("[data-media-image-src]")) {
            String ref = marker.attr("data-media-image-src");
            Element img = marker.selectFirst("img");
            if (img == null) continue;
            if (isMediaRef(ref)) img.attr("src", MEDIA_PATH + ref);
            else img.remove();
        }
        for (Element marker : doc.select("[data-media-document-src]")) {
            String ref = marker.attr("data-media-document-src");
            Element link = marker.selectFirst("a");
            if (link == null) continue;
            if (isMediaRef(ref)) link.attr("href", MEDIA_PATH + ref);
            else link.unwrap();
        }
        for (Element link : doc.select("a[href^=http]")) {
            link.attr("rel", "noopener noreferrer");
        }

        var output = new Document.OutputSettings().prettyPrint(false);
        // a base is needed for the cleaner to judge a relative address; preserveRelativeLinks keeps it as written
        return Jsoup.clean(doc.body().html(), BASE, SAFELIST, output);
    }

    /** The posts this content links to. */
    public Set<String> linkedPosts(String body) {
        if (body == null || body.isBlank()) return Set.of();
        return Jsoup.parseBodyFragment(body).select("a[" + POST_ATTRIBUTE + "]").stream()
                .map(a -> a.attr(POST_ATTRIBUTE))
                .collect(Collectors.toUnmodifiableSet());
    }

    /** At read time: a link to a post that is not here is plain text, and the helper attribute never leaves. */
    public String resolveLinks(String body, Set<String> livePosts) {
        if (body == null || body.isBlank()) return "";
        Document doc = Jsoup.parseBodyFragment(body);
        for (Element link : doc.select("a[" + POST_ATTRIBUTE + "]")) {
            if (livePosts.contains(link.attr(POST_ATTRIBUTE))) {
                link.removeAttr(POST_ATTRIBUTE);
            } else {
                link.unwrap();
            }
        }
        return doc.body().html();
    }

    private static boolean isMediaRef(String ref) {
        return ref != null && ref.matches("[A-Za-z0-9]{1,8}/[A-Za-z0-9]{1,16}");
    }

    private static String lastSegment(String path) {
        if (path == null) return "";
        String trimmed = path.replaceAll("/+$", "");
        String segment = trimmed.substring(trimmed.lastIndexOf('/') + 1);
        return segment.matches("[A-Za-z0-9]{1,16}") ? segment : "";
    }
}
