package com.renaissancerentals.persistence.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table(name = ContactEmailEntity.TABLE_NAME)
public class ContactEmailEntity implements Serializable, RenaissanceEmailAware, Persistable<UUID> {
    public static final String TABLE_NAME = "contact_email";

    @Id
    private UUID id;
    private String name;
    private String fromEmail;
    private String fromPhone;
    private String sourceUrl;

    private boolean phonePreferred;
    private boolean textPreferred;
    private boolean emailPreferred;

    private String rawQuestion;

    private String additionalInfo;

    @Transient
    private boolean isNew;

    private Instant createdAt;

    @Override
    public boolean isNew(){
        return isNew;
    }

    public void markNew(){
        this.isNew = true;
    }
}
