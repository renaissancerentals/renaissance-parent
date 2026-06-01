package com.renaissancerentals.assets.model;

import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

@Builder
public record AssetCreationRequest(String folderId, MultipartFile multipartFile, String name, String description) {}
