package com.renaissancerentals.persistence.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@EqualsAndHashCode(of = "id")
@Table(name = ShortTermFloorplanEntity.TABLE_NAME)
public class ShortTermFloorplanEntity implements Serializable, FloorplanAware {
    public static final String TABLE_NAME = "short_term_floorplan";

    @Id
    private Long id;

    @Column("price_for2to4days")
    private String priceFor2To4Days;

    @Column("price_for5to13days")
    private String priceFor5To13Days;

    @Column("price_for14to29days")
    private String priceFor14To29Days;

    @Column("price_for1to4months")
    private String priceFor1To4Months;

    @Column("price_for4and_more_months")
    private String priceFor4andMoreMonths;

    private Integer squareFoot;

    private String floorplanId;
}
