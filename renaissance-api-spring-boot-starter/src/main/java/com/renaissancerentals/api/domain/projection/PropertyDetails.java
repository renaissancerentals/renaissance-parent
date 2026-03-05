package com.renaissancerentals.api.domain.projection;

import java.util.List;

import com.renaissancerentals.api.domain.Amenity;
import com.renaissancerentals.api.domain.LeasingOffice;
import com.renaissancerentals.api.domain.PropertyBusRoute;
import com.renaissancerentals.api.domain.TeamMember;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@SuppressFBWarnings(value = "EI_EXPOSE_REP", justification = "DTO used only for serialization; safe to expose collections")
public class PropertyDetails {
    private String id;

    private String name;

    private String address;

    private String zipcode;

    private String email;

    private String secondaryEmail;

    private String phone;

    private Float rating;

    private String facebookLink;

    private String twitterLink;

    private String logo;

    private String coverImage;

    private String coverVideo;

    private String propertyFolderId;

    private String photosFolderId;

    private String marketingFolderId;

    private String youtubeLink;

    private String description;

    private String ratingLink;

    private String htmlTitle;

    private String metaDescription;

    private String conversionTrackingId1;

    private String conversionTrackingId2;

    private String analyticsCode;

    private String tawkCode;

    private String pixelCode;

    private String leaseType;

    private String leasingOfficeType;

    private LeasingOffice leasingOffice;

    private List<TeamMember> teamMembers;

    private List<Amenity> amenities;

    private List<PropertyBusRoute> busRoutes;

    private List<FloorplanDetails> floorplans;
}
