package com.renaissancerentals.api.domain.projection;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder
@Data
@EqualsAndHashCode(of = "id")
public class UnitSpotlight {
    private String id;
    private Integer squareFoot;
    private Float rent;
    private String address;
    private String zipcode;
}
