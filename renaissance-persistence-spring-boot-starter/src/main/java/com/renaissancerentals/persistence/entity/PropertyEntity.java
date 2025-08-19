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
@Table(name = PropertyEntity.TABLE_NAME)
public class PropertyEntity implements Serializable {
    public static final String TABLE_NAME = "property";

    @Id
    private String id;

    private String name;

    private String address;

    private String zipcode;

    private String email;

    private String secondaryEmail;

    private String phone;

    private Float rating;

    private String facebookLink;

    private String twitterLink;

    private String logo;

    private String coverImage;

    private String coverVideo;

    private String propertyFolderId;

    private String photosFolderId;

    private String marketingFolderId;

    private String youtubeLink;

    private String description;

    private String ratingLink;

    private String htmlTitle;

    private String metaDescription;

    private String conversionTrackingId1;

    private String conversionTrackingId2;

    private String analyticsCode;

    private String tawkCode;

    private String pixelCode;

    private String leaseType;

    private String leasingOfficeId;

    private String leasingOfficeType;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    private boolean active;

    @Version
    private long version;
}
