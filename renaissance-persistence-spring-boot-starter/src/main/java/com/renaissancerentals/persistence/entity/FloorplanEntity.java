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
@EqualsAndHashCode(of = {"id"})
@Table(name = FloorplanEntity.TABLE_NAME)
public class FloorplanEntity implements Serializable {
    public static final String TABLE_NAME = "floorplan";
    @Id
    private String id;

    private String name;

    private Integer bedroom;

    private Float bathroom;

    private String style;

    private String allowedPet;

    private String petPolicy;

    private Boolean featured;

    private Boolean featuredOnMain;

    private Boolean patioIncluded;

    private Boolean greenCertified;

    private String address;

    private String zipcode;

    private String videoTourLink;

    private String threeSixtyVideoTourLink;

    private String virtualTourLink;

    private String photo;

    private String coverImage;

    private String floorPlanFolderId;

    private String photosFolderId;

    private String marketingFolderId;

    private Integer photosCount;

    private String description;

    private String vanityLink;

    private String htmlTitle;

    private String metaDescription;

    private String conversionTrackingId1;

    private String conversionTrackingId2;

    private String customCode;

    private String highlights;

    private Float specialRent;

    private LocalDate specialRentStartDate;

    private LocalDate specialRentEndDate;

    private String propertyId;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    private Boolean active;

    @Version
    private long version;

}
