package com.renaissancerentals.assets.model;

import org.springframework.web.multipart.MultipartFile;

import lombok.Builder;

@Builder
public record AssetCreationRequest(String folderId, MultipartFile multipartFile, String name, String description) {
}
