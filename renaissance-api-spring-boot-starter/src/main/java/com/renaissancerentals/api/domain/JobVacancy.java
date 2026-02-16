package com.renaissancerentals.api.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.renaissancerentals.api.domain.enumeration.EmploymentType;
import com.renaissancerentals.api.domain.enumeration.SalaryType;

import lombok.Builder;

@Builder
public record JobVacancy(Long id, String title, String description, LocalDateTime validThrough,
        EmploymentType employmentType, Float salary, SalaryType salaryType, String startDate, String workHours,
        LocalDate datePosted) {
}
