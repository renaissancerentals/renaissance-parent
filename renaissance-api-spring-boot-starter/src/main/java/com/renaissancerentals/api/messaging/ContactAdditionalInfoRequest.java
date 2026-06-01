package com.renaissancerentals.api.messaging;

public record ContactAdditionalInfoRequest(
        String amenities,
        String bedrooms,
        String floorPlan,
        String hearAboutUs,
        String lowerRent,
        String upperRent,
        String moveInDate,
        String pets,
        String communities) {

    public ContactAdditionalInfoRequest(ContactAdditionalInfoRequest additionalInfoRequest) {
        this(
                additionalInfoRequest.amenities,
                additionalInfoRequest.bedrooms,
                additionalInfoRequest.floorPlan,
                additionalInfoRequest.hearAboutUs,
                additionalInfoRequest.lowerRent,
                additionalInfoRequest.upperRent,
                additionalInfoRequest.moveInDate,
                additionalInfoRequest.pets,
                additionalInfoRequest.communities);
    }
}
