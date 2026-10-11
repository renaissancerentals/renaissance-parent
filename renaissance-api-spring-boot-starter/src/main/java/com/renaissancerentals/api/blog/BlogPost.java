package com.renaissancerentals.api.blog;

import java.time.OffsetDateTime;

/** A post as this site holds it: sanitized, with its images and links already pointing here. */
public record BlogPost(
        String id,
        String bucket,
        int sourceVersion,
        String title,
        String excerpt,
        String coverRef,
        String category,
        String body,
        OffsetDateTime publishedAt,
        OffsetDateTime updatedAt) {}
