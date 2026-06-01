package com.renaissancerentals.persistence.entity;

import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.Nullable;

@Data
@EqualsAndHashCode(of = {"name", "type", "subType"})
@Table(name = AnalyticsEntity.TABLE_NAME)
public class AnalyticsEntity implements Persistable<String>, Serializable {
    public static final String TABLE_NAME = "analytics";

    @Id
    private String name;

    private String type;

    private String subType;

    private long count;

    private LocalDate createdDate;

    @Transient
    private boolean isNew;

    @Nullable
    @Override
    public String getId() {
        return name;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void markNew() {
        this.isNew = true;
    }
}
