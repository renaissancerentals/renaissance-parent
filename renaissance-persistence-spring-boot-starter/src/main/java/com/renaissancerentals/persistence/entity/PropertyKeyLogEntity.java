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
@Table(name = PropertyKeyLogEntity.TABLE_NAME)
public class PropertyKeyLogEntity implements Serializable {
    public static final String TABLE_NAME = "property_key_log";
    @Id
    private Long id;

    private String box;

    private String tag;

    private String keyDescription;

    private String notes;

    private String propertyId;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

}
