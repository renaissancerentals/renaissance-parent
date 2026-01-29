package com.renaissancerentals.persistence.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Data
@Table(name = ApplicationEmailEntity.TABLE_NAME)
public class ApplicationEmailEntity implements Serializable, RenaissanceEmailAware {
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


    @CreatedDate
    private Instant createdAt;

}
