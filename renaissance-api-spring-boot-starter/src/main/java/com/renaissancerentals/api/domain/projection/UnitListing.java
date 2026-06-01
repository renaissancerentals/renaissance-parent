package com.renaissancerentals.api.domain.projection;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UnitListing {
    private String id;
    private Float rent;
    private Integer squareFoot;
    private LocalDate moveInDate;
    private Integer availabilityExtensionMonths;
}
