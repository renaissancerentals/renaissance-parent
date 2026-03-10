package com.renaissancerentals.api.domain;

import com.renaissancerentals.api.domain.projection.PropertySummary;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;

@Builder
public record Floorplan(
        String id,

        String name,

        Integer bedroom,

        Float bathroom,

        String style,

        String allowedPet,

        String petPolicy,

        Boolean featured,

        Boolean featuredOnMain,

        Boolean patioIncluded,

        Boolean greenCertified,

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

        Float specialRent,

        LocalDate specialRentStartDate,

        LocalDate specialRentEndDate,

        PropertySummary property,

        List<Utility> utilities,

        List<Amenity> amenities,

        List<Unit> units,

        List<WebSpecial> webSpecials

) {
}
