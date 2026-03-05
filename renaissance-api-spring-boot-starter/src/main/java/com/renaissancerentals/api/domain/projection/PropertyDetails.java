package com.renaissancerentals.api.domain.projection;

import java.util.List;

import com.renaissancerentals.api.domain.Amenity;
import com.renaissancerentals.api.domain.LeasingOffice;
import com.renaissancerentals.api.domain.PropertyBusRoute;
import com.renaissancerentals.api.domain.TeamMember;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
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

    public LeasingOffice getLeasingOffice(){
        return leasingOffice == null ? null : leasingOffice.toBuilder().build();
    }

    public List<TeamMember> getTeamMembers(){
        return teamMembers == null ? null : List.copyOf(teamMembers);
    }

    public List<Amenity> getAmenities(){
        return amenities == null ? null : List.copyOf(amenities);
    }

    public List<PropertyBusRoute> getBusRoutes(){
        return busRoutes == null ? null : List.copyOf(busRoutes);
    }

    public List<FloorplanDetails> getFloorplans(){
        return floorplans == null ? null : List.copyOf(floorplans);
    }
}
