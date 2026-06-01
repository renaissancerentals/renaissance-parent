package com.renaissancerentals.persistence.entity;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = ContactEmailEntity.TABLE_NAME)
public class ContactEmailEntity implements Serializable, RenaissanceEmailAware, Persistable<UUID> {
    public static final String TABLE_NAME = "contact_email";

    @Id
    private UUID id;

    private String firstName;
    private String lastName;
    private String fromEmail;
    private String fromPhone;
    private String sourceUrl;

    private boolean phonePreferred;
    private boolean textPreferred;
    private boolean emailPreferred;

    private String rawQuestion;

    private ContactAdditionalInfo additionalInfo;

    @Transient
    private boolean isNew;

    private Instant createdAt;

    @Override
    public boolean isNew() {
        return isNew;
    }

    public void markNew() {
        this.isNew = true;
    }

    public ContactAdditionalInfo getAdditionalInfo() {
        if (additionalInfo == null) return null;
        return new ContactAdditionalInfo(
                additionalInfo.getAmenities(),
                additionalInfo.getBedrooms(),
                additionalInfo.getFloorPlan(),
                additionalInfo.getHearAboutUs(),
                additionalInfo.getLowerRent(),
                additionalInfo.getUpperRent(),
                additionalInfo.getMoveInDate(),
                additionalInfo.getPets(),
                additionalInfo.getCommunities());
    }
}
