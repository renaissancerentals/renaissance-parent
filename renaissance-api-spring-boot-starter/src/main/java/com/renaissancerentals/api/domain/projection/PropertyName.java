package com.renaissancerentals.api.domain.projection;

import lombok.Builder;

@Builder
public record PropertyName(String id, String name) {
}
