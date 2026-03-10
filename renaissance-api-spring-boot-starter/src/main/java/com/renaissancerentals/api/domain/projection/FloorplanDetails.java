package com.renaissancerentals.api.domain.projection;

import java.time.LocalDate;
import java.util.List;

import com.renaissancerentals.api.domain.Amenity;
import com.renaissancerentals.api.domain.WebSpecial;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "DTO used only for serialization; safe to expose collections")
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

    private Integer photosCount;
    private String description;
    private String vanityLink;
    private String htmlTitle;
    private String metaDescription;
    private String conversionTrackingId1;
    private String conversionTrackingId2;
    private String customCode;
    private String highlights;

    private List<Amenity> amenities;
    private List<UnitDetails> units;
    private List<WebSpecial> webSpecials;

}
