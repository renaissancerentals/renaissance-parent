package com.renaissancerentals.persistence.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
@Table(name = MileageEntity.TABLE_NAME)
public class MileageEntity implements Serializable {
    public static final String TABLE_NAME = "mileage";
    @Id
    private Long id;

    private LocalDate driveDate;

    @CreatedBy
    private String employee;

    private Integer startingMileage;

    private Integer endingMileage;

    private String notes;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
