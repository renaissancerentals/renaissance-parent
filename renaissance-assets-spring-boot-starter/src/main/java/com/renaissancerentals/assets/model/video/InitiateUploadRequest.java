package com.renaissancerentals.assets.model.video;

public record InitiateUploadRequest(String folderId, String name, String description, String mimeType) {
}
