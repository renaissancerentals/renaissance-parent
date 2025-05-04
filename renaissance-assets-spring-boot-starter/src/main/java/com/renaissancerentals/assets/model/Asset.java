package com.renaissancerentals.assets.model;

import lombok.Builder;

@Builder
public record Asset(String id, String name, String description, String folderId, Integer height, Integer width,
        String mimeType) {
}
