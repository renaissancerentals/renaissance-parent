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
@Table(name = WebSpecialEntity.TABLE_NAME)
public class WebSpecialEntity implements Serializable, FloorplanAware {
    public static final String TABLE_NAME = "web_special";
    @Id
    private Long id;

    private LocalDate startDate;

    private LocalDate endDate;

    private String description;

    private String floorplanId;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

}
