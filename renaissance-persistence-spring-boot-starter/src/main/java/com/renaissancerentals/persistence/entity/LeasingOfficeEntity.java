package com.renaissancerentals.persistence.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
@Table(name = LeasingOfficeEntity.TABLE_NAME)
public class LeasingOfficeEntity implements Serializable {
    public static final String TABLE_NAME = "leasing_office";

    @Id
    private String id;

    private String name;

    private String address;

    private String zipcode;

    private String phone;

    private String officeHours;

    private String direction;

    private String officeMap;

    private String officeMapLandscape;

    private String officeImage;

    private String officeImageDescription;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @Version
    private long version;
}
