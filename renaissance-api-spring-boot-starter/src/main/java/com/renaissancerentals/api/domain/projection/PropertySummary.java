package com.renaissancerentals.api.domain.projection;

import com.renaissancerentals.api.domain.PropertyBusRoute;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class PropertySummary {
    private String id;

    private String name;

    private String address;

    private String zipcode;

    private String email;

    private String phone;

    private String leaseType;

    private List<PropertyBusRoute> busRoutes;
}
