package com.renaissancerentals.api.domain;

import lombok.Builder;

@Builder
public record Testimonial(String tenant, String testimonial) {
}
