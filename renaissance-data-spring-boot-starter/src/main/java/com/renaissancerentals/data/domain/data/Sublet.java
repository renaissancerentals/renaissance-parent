package com.renaissancerentals.data.domain.data;

import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record Sublet(Long id, String assetKey, String firstName, String lastName, String email, Integer bedroom,
                     Integer availableBedrooms, LocalDate availableFrom, LocalDate availableTo, Float rent,
                     boolean petsAllowed, boolean utilitiesIncluded, String address, String zipcode,
                     String subletFolderId, String photosFolderId, String coverImage, String title, String description,
                     LocalDateTime createdDate, boolean active, boolean approved, long version) {
}

