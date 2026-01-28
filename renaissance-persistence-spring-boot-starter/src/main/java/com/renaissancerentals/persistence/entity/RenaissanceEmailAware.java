package com.renaissancerentals.persistence.entity;

import java.time.Instant;
import java.util.UUID;

public interface RenaissanceEmailAware {

    UUID getId();

    String getName();

    String getFromEmail();

    String getFromPhone();

    String getSourceUrl();

    Instant getCreatedAt();
}
