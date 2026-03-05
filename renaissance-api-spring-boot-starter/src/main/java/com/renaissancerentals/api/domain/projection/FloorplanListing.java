package com.renaissancerentals.api.domain.projection;

import java.time.LocalDate;
import java.util.List;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FloorplanListing {
    private String id;
    private String name;
    private Integer bedroom;
    private Float bathroom;
    private String coverImage;
    private Boolean featured;
    private String style;
    private Float specialRent;
    private LocalDate specialRentStartDate;
    private LocalDate specialRentEndDate;
    private String address;
    private String zipcode;
    private String videoTourLink;
    private String virtualTourLink;
    private String photosFolderId;
    private List<String> webSpecials;
    private List<UnitListing> units;

    public List<String> getWebSpecials(){
        return webSpecials != null ? List.copyOf(webSpecials) : null;
    }

    public List<UnitListing> getUnits(){
        return units != null ? List.copyOf(units) : null;
    }
}
