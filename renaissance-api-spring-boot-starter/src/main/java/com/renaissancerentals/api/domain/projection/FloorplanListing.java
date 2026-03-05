package com.renaissancerentals.api.domain.projection;

import java.time.LocalDate;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "DTO used only for serialization; safe to expose collections")
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

}
