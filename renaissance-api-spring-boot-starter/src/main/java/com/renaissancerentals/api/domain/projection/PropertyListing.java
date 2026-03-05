package com.renaissancerentals.api.domain.projection;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PropertyListing {
    private String id;
    private String name;
    private List<FloorplanListing> floorplans;

    public List<FloorplanListing> getFloorplans(){
        return floorplans == null ? null : List.copyOf(floorplans);
    }
}
