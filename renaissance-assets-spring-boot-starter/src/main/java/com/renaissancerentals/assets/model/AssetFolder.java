package com.renaissancerentals.assets.model;

import lombok.Builder;

@Builder
public record AssetFolder(String folderId, String name) {}
