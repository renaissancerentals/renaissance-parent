package com.renaissancerentals.persistence.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
@Table(name = PropertyAmenityEntity.TABLE_NAME)
public class PropertyAmenityEntity implements Serializable, PropertyAware {
    public static final String TABLE_NAME = "property_amenity";
    @Id
    private Long id;

    private String name;

    private String type;

    private String propertyId;

    private boolean featured;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

}
