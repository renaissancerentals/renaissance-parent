package com.renaissancerentals.assets.model;

import org.springframework.web.multipart.MultipartFile;

public record AssetUpdateRequest(String id, MultipartFile multipartFile, String name, String description) {
}
