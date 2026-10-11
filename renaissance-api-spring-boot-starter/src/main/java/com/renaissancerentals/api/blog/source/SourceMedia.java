package com.renaissancerentals.api.blog.source;

/** An image or document, copied. */
public record SourceMedia(String contentType, byte[] content) {

    public SourceMedia {
        content = content.clone();
    }

    @Override
    public byte[] content() {
        return content.clone();
    }
}
