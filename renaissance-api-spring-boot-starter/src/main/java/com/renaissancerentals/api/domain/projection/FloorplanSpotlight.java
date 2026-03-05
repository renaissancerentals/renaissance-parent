package com.renaissancerentals.api.domain.projection;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FloorplanSpotlight {
    private String id;
    private String name;
    private String metaDescription;
    private Integer bedroom;
    private Float bathroom;
    private String style;
    private String coverImage;
    private List<UnitSpotlight> units;
    private PropertySpotlight property;

    public List<UnitSpotlight> getUnits(){
        return units != null ? List.copyOf(units) : null;
    }

    public PropertySpotlight getProperty(){
        return property == null
                ? null
                : new PropertySpotlight(property.getId(), property.getName(), property.getAddress(),
                        property.getZipcode());
    }
}
