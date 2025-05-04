package com.renaissancerentals.assets.model;

import org.springframework.web.multipart.MultipartFile;

public record AssetCreationRequest(String folderId, MultipartFile multipartFile, String name, String description) {
}
