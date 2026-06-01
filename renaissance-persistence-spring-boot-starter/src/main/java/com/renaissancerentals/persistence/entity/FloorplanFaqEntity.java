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
@Table(name = FloorplanFaqEntity.TABLE_NAME)
public class FloorplanFaqEntity implements Serializable, FloorplanAware {
    public static final String TABLE_NAME = "floorplan_faq";

    @Id
    private Long id;

    private String question;

    private String answer;

    private Float sortOrder;

    private String floorplanId;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
