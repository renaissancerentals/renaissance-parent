package com.renaissancerentals.api.domain;

import com.renaissancerentals.api.domain.projection.PropertySummary;
import lombok.Builder;

import java.util.List;

@Builder
public record ShortTermFloorplan(
        String id,

        String name,

        Integer bedroom,

        Float bathroom,

        String style,

        String address,

        String zipcode,

        String videoTourLink,

        String threeSixtyVideoTourLink,

        String virtualTourLink,

        String photo,

        String coverImage,

        String floorPlanFolderId,

        String photosFolderId,

        Integer photosCount,

        String description,

        String vanityLink,

        String htmlTitle,

        String metaDescription,

        String conversionTrackingId1,

        String conversionTrackingId2,

        String customCode,

        String highlights,

        PropertySummary property,

        List<Amenity> amenities,

        String priceFor2To4Days,

        String priceFor5To13Days,

        String priceFor14To29Days,

        String priceFor1To4Months,

        String priceFor4andMoreMonths,

        Integer squareFoot
) {
}
