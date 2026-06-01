package com.renaissancerentals.api.messaging;

import com.renaissancerentals.api.domain.enumeration.ContactEventType;

public record ContactEventRequest(String property, ContactEventType type) {}
