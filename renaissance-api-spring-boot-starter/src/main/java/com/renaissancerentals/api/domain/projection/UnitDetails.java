package com.renaissancerentals.api.domain.projection;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UnitDetails {
    private String id;

    private Integer squareFoot;

    private String allowedPet;

    private Float rent;

    private Float discountedRent;

    private Float deposit;

    private Boolean furnished;

    private Integer garages;

    private LocalDate moveInDate;

    private Integer availabilityExtensionMonths;
}
