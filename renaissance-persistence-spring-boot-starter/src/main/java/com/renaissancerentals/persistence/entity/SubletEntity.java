package com.renaissancerentals.persistence.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.relational.core.mapping.Table;

@Data
@EqualsAndHashCode(of = "id")
@Table(name = SubletEntity.TABLE_NAME)
public class SubletEntity implements Serializable {
    public static final String TABLE_NAME = "sublet";

    @Id
    private Long id;

    private String assetKey;

    private String firstName;

    private String lastName;

    private String email;

    private Integer bedroom;

    private Integer availableBedrooms;

    private LocalDate availableFrom;

    private LocalDate availableTo;

    private Float rent;

    private Boolean petsAllowed;

    private Boolean utilitiesIncluded;

    private String address;

    private String zipcode;

    private String subletFolderId;

    private String photosFolderId;

    private String coverImage;

    private String title;

    private String description;

    @CreatedDate
    private LocalDateTime createdDate;

    private Boolean active;

    private boolean approved;

    @Version
    private long version;
}
