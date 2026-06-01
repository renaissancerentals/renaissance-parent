package com.renaissancerentals.assets.model.video;

import lombok.Builder;

@Builder
public record VideoUploadMetadata(String uploadId, String uploadUrl) {}
