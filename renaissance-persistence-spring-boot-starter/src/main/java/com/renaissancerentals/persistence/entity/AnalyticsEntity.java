package com.renaissancerentals.persistence.entity;

import java.io.Serializable;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"name", "type", "subType"})
@Table(name = AnalyticsEntity.TABLE_NAME)
public class AnalyticsEntity implements Serializable {
    public static final String TABLE_NAME = "analytics";
    @Id
    private String name;

    private String type;

    private String subType;

    private long count;

    private LocalDate createdDate;
}
