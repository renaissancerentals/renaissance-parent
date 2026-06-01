package com.renaissancerentals.api.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PropertyBusRoute {
    private String id;
    private String busRoute;
    private String busRouteLink;
}
