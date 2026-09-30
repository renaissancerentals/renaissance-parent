package com.renaissancerentals.foundation.seo;

import java.util.List;
import java.util.Map;

/** Everything needed to render the head (and a crawler friendly snapshot) of a page. */
public record PageMeta(
        String title,
        String description,
        String canonicalUrl,
        boolean indexable,
        int status,
        String ogType,
        String image,
        List<Map<String, Object>> jsonLd,
        String snapshotHtml) {

    public PageMeta {
        jsonLd = jsonLd == null ? List.of() : List.copyOf(jsonLd);
        ogType = ogType == null ? "website" : ogType;
    }

    public PageMeta withJsonLd(List<Map<String, Object>> jsonLd) {
        return new PageMeta(title, description, canonicalUrl, indexable, status, ogType, image, jsonLd, snapshotHtml);
    }

    public PageMeta withSnapshot(String snapshotHtml) {
        return new PageMeta(title, description, canonicalUrl, indexable, status, ogType, image, jsonLd, snapshotHtml);
    }
}
