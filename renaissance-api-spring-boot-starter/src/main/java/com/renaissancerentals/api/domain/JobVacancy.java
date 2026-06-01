package com.renaissancerentals.api.domain;

import com.renaissancerentals.api.domain.enumeration.EmploymentType;
import com.renaissancerentals.api.domain.enumeration.SalaryType;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record JobVacancy(
        Long id,
        String title,
        String description,
        LocalDateTime validThrough,
        EmploymentType employmentType,
        Float salary,
        SalaryType salaryType,
        String startDate,
        String workHours,
        LocalDate datePosted) {}
