package com.renaissancerentals.persistence.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
@Table(name = HomePageSpecialEntity.TABLE_NAME)
public class HomePageSpecialEntity implements Serializable {
    public static final String TABLE_NAME = "home_page_special";
    @Id
    private Long id;

    private String title;

    private String description;

    private String details;

    private String information1;

    private String information2;

    private String information3;

    private String image;

    private LocalDate startDate;

    private LocalDate endDate;

    private String properties;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

}
