package com.renaissancerentals.assets.model.video;

import lombok.Builder;

@Builder
public record InitiateUploadRequest(String folderId, String name, String description, String mimeType) {
}
