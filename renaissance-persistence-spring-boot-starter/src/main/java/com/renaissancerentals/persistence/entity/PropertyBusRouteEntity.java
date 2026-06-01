package com.renaissancerentals.persistence.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@EqualsAndHashCode(of = "id")
@Table(name = PropertyBusRouteEntity.TABLE_NAME)
public class PropertyBusRouteEntity implements Serializable, PropertyAware {
    public static final String TABLE_NAME = "property_bus_route";

    @Id
    private Long id;

    private String busRoute;

    private String busRouteLink;

    private String propertyId;
}
