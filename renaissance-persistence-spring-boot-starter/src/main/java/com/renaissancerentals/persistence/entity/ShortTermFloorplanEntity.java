package com.renaissancerentals.persistence.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
@Table(name = ShortTermFloorplanEntity.TABLE_NAME)
public class ShortTermFloorplanEntity implements Serializable, FloorplanAware {
    public static final String TABLE_NAME = "short_term_floorplan";
    @Id
    private Long id;

    private String priceFor2To4Days;

    private String priceFor5To13Days;

    private String priceFor14To29Days;

    private String priceFor1To4Months;

    private String priceFor4andMoreMonths;

    private Integer squareFoot;

    private String floorplanId;

}
