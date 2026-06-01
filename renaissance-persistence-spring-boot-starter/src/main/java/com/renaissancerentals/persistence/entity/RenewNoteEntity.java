package com.renaissancerentals.persistence.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@EqualsAndHashCode(of = "id")
@Table(name = RenewNoteEntity.TABLE_NAME)
public class RenewNoteEntity implements Serializable {
    public static final String TABLE_NAME = "renew_note";

    @Id
    @Column("unit_id")
    private String id;

    private String renewType;

    private LocalDate extensionDate;

    private String updatedBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
