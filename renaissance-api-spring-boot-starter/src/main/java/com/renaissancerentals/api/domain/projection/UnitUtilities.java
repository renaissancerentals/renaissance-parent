package com.renaissancerentals.api.domain.projection;

import java.util.List;

import com.renaissancerentals.api.domain.Utility;

import lombok.Builder;

@Builder
public record UnitUtilities(String id, String propertyEmail, List<Utility> utilities) {
    public UnitUtilities {
        utilities = utilities == null ? List.of() : List.copyOf(utilities);
    }
}
