package com.renaissancerentals.persistence.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

@Data
@EqualsAndHashCode(of = "id")
@Table(name = TestimonialEntity.TABLE_NAME)
public class TestimonialEntity implements Serializable, FloorplanAware {
    public static final String TABLE_NAME = "testimonial";

    @Id
    private Long id;

    private String tenant;

    private String testimonial;

    private String floorplanId;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
