package com.renaissancerentals.api.domain;

import java.time.LocalDate;
import java.util.List;

public record HomePageSpecial(
        Long id,
        String description,
        String image,
        LocalDate startDate,
        LocalDate endDate,
        String properties,
        List<HomePageLinkedImageRegion> links) {

    public HomePageSpecial {
        links = links == null ? List.of() : List.copyOf(links);
    }
}
