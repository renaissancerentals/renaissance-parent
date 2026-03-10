package com.renaissancerentals.api.domain;

import java.time.LocalDate;

public record HomePageSpecial(Long id,

        String title,

        String description,

        String details,

        String information1,

        String information2,

        String information3,

        String image,

        LocalDate startDate,

        LocalDate endDate,

        String properties) {
}
