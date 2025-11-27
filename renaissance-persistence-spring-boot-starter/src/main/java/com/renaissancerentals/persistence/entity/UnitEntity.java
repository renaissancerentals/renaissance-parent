package com.renaissancerentals.persistence.entity;

import java.io.Serializable;
import java.time.LocalDate;
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
@Table(name = UnitEntity.TABLE_NAME)
public class UnitEntity implements Serializable {
    public static final String TABLE_NAME = "unit";

    @Id
    private String id;

    private Integer squareFoot;

    private String allowedPet;

    private String petPolicy;

    private Float rent;

    private Float discountedRent;

    private LocalDate discountedRentStartDate;

    private LocalDate discountedRentEndDate;

    private String discountedRentDescription;

    private Float deposit;

    private Boolean endUnit;

    private Boolean furnished;

    private Boolean murphyBedProvided;

    private Boolean affordableHousing;

    private Boolean patioIncluded;

    private String level;

    private String turnoverRate;

    private String features;

    private String address;

    private String zipcode;

    private Integer garages;

    private LocalDate moveInDate;

    private Integer availabilityExtensionMonths;

    private String unitFolderId;

    private String photosFolderId;

    private String marketingFolderId;

    private Integer photosCount;

    private String coverImage;

    private String videoTourLink;

    private String threeSixtyVideoTourLink;

    private String virtualTourLink;

    private String photosLink;

    private String floorplanLink;

    private String floorplanId;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    private Boolean active;

    @Version
    private long version;

}
