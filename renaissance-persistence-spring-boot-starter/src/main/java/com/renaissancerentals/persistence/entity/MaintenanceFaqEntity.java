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
@Table(name = MaintenanceFaqEntity.TABLE_NAME)
public class MaintenanceFaqEntity implements Serializable {
    public static final String TABLE_NAME = "maintenance_faq";

    @Id
    private Long id;

    private String question;

    private String answer;

    private Float sortOrder;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
