package com.renaissancerentals.api.domain.projection;

import java.util.List;

import lombok.Builder;

@Builder
public record LeasingOfficeDetails(String id, String name, String address, String zipcode, String phone,
        String officeHours, String direction, String officeMap, String officeMapLandscape, String officeImage,
        String officeImageDescription, List<PropertyLeasingOffice> properties) {
    public LeasingOfficeDetails {
        properties = properties == null ? List.of() : List.copyOf(properties);
    }
}
