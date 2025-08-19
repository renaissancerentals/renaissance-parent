package com.renaissancerentals.data.domain.data;

import com.renaissancerentals.data.domain.data.enumeration.EmploymentType;
import com.renaissancerentals.data.domain.data.enumeration.SalaryType;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record JobVacancy(Long id, String title, String description, LocalDateTime validThrough,
                         EmploymentType employmentType, Float salary, SalaryType salaryType, String startDate,
                         String workHours, String lastModifiedBy, LocalDate datePosted, LocalDateTime lastModifiedDate,
                         boolean active) {
}

