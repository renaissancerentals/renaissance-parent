package com.renaissancerentals.api.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Amenity {
    private Long id;
    private String name;
    private String type;
    private boolean featured;
}
