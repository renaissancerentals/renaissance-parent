package com.renaissancerentals.api.domain.projection;

import com.renaissancerentals.api.domain.Utility;
import java.util.List;
import lombok.Builder;

@Builder
public record UnitUtilities(String id, String propertyEmail, List<Utility> utilities) {
    public UnitUtilities {
        utilities = utilities == null ? List.of() : List.copyOf(utilities);
    }
}
