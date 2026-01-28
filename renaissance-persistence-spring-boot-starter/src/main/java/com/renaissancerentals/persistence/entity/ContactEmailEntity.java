package com.renaissancerentals.persistence.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;

@Data
@Table(name = ContactEmailEntity.TABLE_NAME)
public class ContactEmailEntity implements Serializable, RenaissanceEmailAware {
    public static final String TABLE_NAME = "contact_email";

    @Id
    private UUID id;
    private String name;
    private String fromEmail;
    private String fromPhone;
    private String sourceUrl;
    @CreatedDate
    private Instant createdAt;

    private boolean phonePreferred;
    private boolean textPreferred;
    private boolean emailPreferred;

    private String rawQuestion;

    private Map<String, Object> additionalInfo;

    public Map<String, Object> getAdditionalInfo(){
        return additionalInfo == null ? null : new HashMap<>(additionalInfo);
    }
}
