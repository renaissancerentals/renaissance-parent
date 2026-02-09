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
@Table(name = ApplicationEmailEntity.TABLE_NAME)
public class ApplicationEmailEntity implements Serializable, RenaissanceEmailAware, Persistable<UUID> {
    public static final String TABLE_NAME = "application_email";

    @Id
    private UUID id;
    private String name;
    private String fromEmail;
    private String fromPhone;
    private String sourceUrl;

    private String interestedCommunity;
    private String interestedLocation;
    private String rawQuestions;

    private Instant createdAt;

    @Transient
    private boolean isNew;

    @Override
    public boolean isNew(){
        return isNew;
    }

    public void markNew(){
        this.isNew = true;
    }
}
