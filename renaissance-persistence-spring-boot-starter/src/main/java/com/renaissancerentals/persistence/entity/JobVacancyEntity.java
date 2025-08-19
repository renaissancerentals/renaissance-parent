package com.renaissancerentals.persistence.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
@Table(name = JobVacancyEntity.TABLE_NAME)
public class JobVacancyEntity {
    public static final String TABLE_NAME = "job_vacancy";
    @Id
    private Long id;

    private String title;

    private String description;

    private LocalDateTime validThrough;

    private String employmentType;

    private Float salary;

    private String salaryType;

    private String startDate;

    private String workHours;

    @LastModifiedBy
    private String lastModifiedBy;

    @CreatedDate
    private LocalDate datePosted;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    private boolean active;
}
