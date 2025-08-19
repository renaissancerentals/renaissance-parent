package com.renaissancerentals.persistence.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Table(name = PendingNoteEntity.TABLE_NAME)
@EqualsAndHashCode(of = "id")
public class PendingNoteEntity implements Serializable {
    public static final String TABLE_NAME = "pending_note";
    @Id
    @Column("unit_id")
    private String id;

    private String note;

    private LocalDate dueDate;

    private String updatedBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

}
