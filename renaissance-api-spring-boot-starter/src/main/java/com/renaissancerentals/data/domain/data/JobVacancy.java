package com.renaissancerentals.data.domain.data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.renaissancerentals.data.domain.data.enumeration.EmploymentType;
import com.renaissancerentals.data.domain.data.enumeration.SalaryType;

import lombok.Builder;

@Builder
public record JobVacancy(Long id, String title, String description, LocalDateTime validThrough,
        EmploymentType employmentType, Float salary, SalaryType salaryType, String startDate, String workHours,
        LocalDate datePosted) {
}
