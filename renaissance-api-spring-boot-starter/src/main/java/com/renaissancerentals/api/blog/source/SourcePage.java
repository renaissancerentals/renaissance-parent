package com.renaissancerentals.api.blog.source;

import java.util.List;

/** A page of changes and the cursor to ask for next (and to acknowledge once the page is committed). */
public record SourcePage(List<SourceItem> items, String nextCursor, boolean hasMore) {

    public SourcePage {
        items = items == null ? List.of() : List.copyOf(items);
    }
}
