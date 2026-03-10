package com.renaissancerentals.api.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class LeasingOffice {
    private String id;
    private String name;
    private String address;
    private String zipcode;
    private String phone;
    private String officeHours;
    private String direction;
    private String officeMap;
    private String officeMapLandscape;
    private String officeImage;
    private String officeImageDescription;
}
