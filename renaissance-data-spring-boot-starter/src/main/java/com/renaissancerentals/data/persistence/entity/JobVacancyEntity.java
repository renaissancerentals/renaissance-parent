package com.renaissancerentals.data.persistence.entity;


import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
