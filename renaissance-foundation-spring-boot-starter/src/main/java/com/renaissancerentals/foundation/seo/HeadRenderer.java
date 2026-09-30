package com.renaissancerentals.foundation.seo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Rewrites the head of the single page app shell so that non JavaScript crawlers see real page metadata. */
public class HeadRenderer {

    private static final Pattern TITLE = Pattern.compile("(?is)<title>.*?</title>");
    private static final Pattern DESCRIPTION = Pattern.compile("(?is)<meta\\s+name=[\"']description[\"'][^>]*>");
    private static final Pattern ROOT = Pattern.compile("<div id=\"root\"\\s*></div>");

    private final ObjectMapper mapper;
    private final SeoProperties properties;

    public HeadRenderer(ObjectMapper mapper, SeoProperties properties) {
        this.mapper = mapper;
        this.properties = properties;
    }

    public String render(String html, PageMeta meta) {
        var withoutOld =
                DESCRIPTION.matcher(TITLE.matcher(html).replaceFirst("")).replaceFirst("");
        var head = withoutOld.indexOf("</head>");
        if (head < 0) {
            return html;
        }
        var result = withoutOld.substring(0, head) + block(meta) + withoutOld.substring(head);
        if (!SeoText.blank(meta.snapshotHtml())) {
            result = ROOT.matcher(result)
                    .replaceFirst(Matcher.quoteReplacement(
                            "<div id=\"root\"><div data-seo-snapshot hidden>" + meta.snapshotHtml() + "</div></div>"));
        }
        return result;
    }

    private String block(PageMeta meta) {
        var out = new StringBuilder("\n");
        out.append("<title>").append(SeoText.esc(meta.title())).append("</title>\n");
        tag(out, "<meta name=\"description\" content=\"%s\"/>", meta.description());
        out.append("<link rel=\"canonical\" href=\"")
                .append(SeoText.esc(meta.canonicalUrl()))
                .append("\"/>\n");
        out.append("<meta name=\"robots\" content=\"")
                .append(meta.indexable() ? "index, follow, max-image-preview:large" : "noindex, follow")
                .append("\"/>\n");
        tag(out, "<meta property=\"og:site_name\" content=\"%s\"/>", properties.siteName());
        tag(out, "<meta property=\"og:type\" content=\"%s\"/>", meta.ogType());
        tag(out, "<meta property=\"og:title\" content=\"%s\"/>", meta.title());
        tag(out, "<meta property=\"og:description\" content=\"%s\"/>", meta.description());
        tag(out, "<meta property=\"og:url\" content=\"%s\"/>", meta.canonicalUrl());
        tag(out, "<meta property=\"og:image\" content=\"%s\"/>", meta.image());
        out.append("<meta property=\"og:locale\" content=\"en_US\"/>\n");
        out.append("<meta name=\"twitter:card\" content=\"")
                .append(SeoText.blank(meta.image()) ? "summary" : "summary_large_image")
                .append("\"/>\n");
        tag(out, "<meta name=\"twitter:title\" content=\"%s\"/>", meta.title());
        tag(out, "<meta name=\"twitter:description\" content=\"%s\"/>", meta.description());
        tag(out, "<meta name=\"twitter:image\" content=\"%s\"/>", meta.image());
        if (!meta.jsonLd().isEmpty()) {
            out.append("<script type=\"application/ld+json\">")
                    .append(json(graph(meta)))
                    .append("</script>\n");
        }
        return out.toString();
    }

    /** One script with a single @graph, instead of one script per node. */
    private static Map<String, Object> graph(PageMeta meta) {
        var nodes = new ArrayList<Map<String, Object>>();
        for (var node : meta.jsonLd()) {
            var copy = new LinkedHashMap<>(node);
            copy.remove("@context");
            nodes.add(copy);
        }
        var graph = new LinkedHashMap<String, Object>();
        graph.put("@context", "https://schema.org");
        graph.put("@graph", nodes);
        return graph;
    }

    private static void tag(StringBuilder out, String template, String value) {
        if (!SeoText.blank(value)) {
            out.append(String.format(template, SeoText.esc(value))).append('\n');
        }
    }

    private String json(Object node) {
        try {
            // "<" is escaped so that no value can close the script element
            return mapper.writeValueAsString(node).replace("<", "\\u003c");
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot serialise JSON-LD", e);
        }
    }
}
