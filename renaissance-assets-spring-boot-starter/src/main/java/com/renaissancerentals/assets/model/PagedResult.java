package com.renaissancerentals.assets.model;

import java.util.List;
import lombok.Builder;

@Builder
public record PagedResult<T>(List<T> items, String nextPageToken, int pageSize) {
    public PagedResult {
        items = items == null ? List.of() : List.copyOf(items);
    }
}
