package com.renaissancerentals.api.domain.projection;

import java.time.LocalDate;
import java.util.List;

import com.renaissancerentals.api.domain.Amenity;
import com.renaissancerentals.api.domain.WebSpecial;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class FloorplanDetails {
    private String id;
    private String name;
    private Integer bedroom;
    private Float bathroom;
    private String style;
    private Float specialRent;
    private LocalDate specialRentStartDate;
    private LocalDate specialRentEndDate;
    private String address;
    private String zipcode;
    private Boolean featured;
    private Boolean greenCertified;
    private String videoTourLink;
    private String threeSixtyVideoTourLink;
    private String virtualTourLink;
    private String photo;
    private String coverImage;
    private String floorPlanFolderId;
    private String photosFolderId;
    private List<Amenity> amenities;
    private List<UnitDetails> units;
    private List<WebSpecial> webSpecials;

    public List<WebSpecial> getWebSpecials(){
        return webSpecials != null ? List.copyOf(webSpecials) : null;
    }

    public List<UnitDetails> getUnits(){
        return units != null ? List.copyOf(units) : null;
    }

    public List<Amenity> getAmenities(){
        return amenities != null ? List.copyOf(amenities) : null;
    }
}
