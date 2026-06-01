package com.renaissancerentals.assets.model.video;

import lombok.Builder;

@Builder
public record VideoAsset(
        String id,
        String name,
        String description,
        String folderId,
        String mimeType,
        String videoUrl,
        String thumbnailUrl) {}
