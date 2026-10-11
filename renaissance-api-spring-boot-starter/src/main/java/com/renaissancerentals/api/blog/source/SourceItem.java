package com.renaissancerentals.api.blog.source;

import java.time.OffsetDateTime;
import java.util.List;

/** One post as the notes platform describes it. A REMOVED item is a tombstone: only id, version and time are set. */
public record SourceItem(
        String id,
        int version,
        String status,
        OffsetDateTime at,
        String title,
        String excerpt,
        String cover,
        String category,
        OffsetDateTime publishedAt,
        OffsetDateTime updatedAt,
        String contentHtml,
        List<String> media) {

    public SourceItem {
        media = media == null ? List.of() : List.copyOf(media);
    }

    public boolean removed() {
        return "REMOVED".equals(status);
    }
}
