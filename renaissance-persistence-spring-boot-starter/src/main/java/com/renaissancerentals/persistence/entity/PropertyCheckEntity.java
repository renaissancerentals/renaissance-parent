package com.renaissancerentals.persistence.entity;

import java.io.Serializable;
import java.time.LocalDate;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
@Table(name = PropertyCheckEntity.TABLE_NAME)
public class PropertyCheckEntity implements Serializable {
    public static final String TABLE_NAME = "property_check";
    @Id
    private Long id;

    @CreatedBy
    private String employee;

    private LocalDate startDate;

    private String startTime;

    private String startAssetId;

    private String startAddress;

    private LocalDate stopDate;

    private String stopTime;

    private String stopAddress;

    private String stopAssetId;

    private String notes;

}
