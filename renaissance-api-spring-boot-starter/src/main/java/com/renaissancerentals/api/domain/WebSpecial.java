package com.renaissancerentals.api.domain;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WebSpecial {
    private Long id;

    private LocalDate startDate;

    private LocalDate endDate;

    private String description;
}
