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
@Table(name = LeaseEntity.TABLE_NAME)
public class LeaseEntity implements Serializable, UnitAware {
    public static final String TABLE_NAME = "lease";
    @Id
    private Long id;

    private String tenant;

    private LocalDate startDate;

    private LocalDate endDate;

    private LocalDate signedDate;

    private String unitId;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

}
