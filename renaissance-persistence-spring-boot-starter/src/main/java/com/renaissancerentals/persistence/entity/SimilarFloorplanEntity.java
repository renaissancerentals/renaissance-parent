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
@Table(name = SimilarFloorplanEntity.TABLE_NAME)
@EqualsAndHashCode(of = "id")
public class SimilarFloorplanEntity implements Serializable, FloorplanAware {
    public static final String TABLE_NAME = "similar_floorplan";
    @Id
    private Long id;

    private String floorplanId;

    private String similarFloorplanId;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

}
