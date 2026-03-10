package com.renaissancerentals.api.domain.projection;

import lombok.Builder;

@Builder
public record PropertyLeasingOffice(String id, String name, String leasingOfficeType) {
}
