package com.renaissancerentals.api.domain.projection;

import java.time.LocalDate;

import com.renaissancerentals.api.domain.Floorplan;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UnitFloorplan {
    private String id;

    private Integer squareFoot;

    private String allowedPet;

    private String petPolicy;

    private Float rent;

    private Float discountedRent;

    private LocalDate discountedRentStartDate;

    private LocalDate discountedRentEndDate;

    private String discountedRentDescription;

    private Float deposit;

    private Boolean endUnit;

    private Boolean furnished;

    private Boolean murphyBedProvided;

    private Boolean affordableHousing;

    private Boolean patioIncluded;

    private String level;

    private String turnoverRate;

    private String features;

    private String address;

    private String zipcode;

    private Integer garages;

    private LocalDate moveInDate;

    private Integer availabilityExtensionMonths;

    private String unitFolderId;

    private String photosFolderId;

    private String marketingFolderId;

    private Integer photosCount;

    private String coverImage;

    private String floorplanImage;

    private String videoTourLink;

    private String threeSixtyVideoTourLink;

    private String virtualTourLink;

    private String photosLink;

    private String floorplanLink;

    private Floorplan floorplan;

}
