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
@Table(name = UtilityEntity.TABLE_NAME)
public class UtilityEntity implements Serializable, FloorplanAware {
    public static final String TABLE_NAME = "utility";
    @Id
    private Long id;

    private String name;

    private String floorplanId;

    private String type;

    private Float averageMonthlyBill;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

}
