package com.renaissancerentals.api.domain.projection;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PropertySpotlight {
    private String id;

    private String name;

    private String address;

    private String zipcode;

}
