package com.renaissancerentals.api.domain.template;

import lombok.Builder;

@Builder
public record ContactAcknowledgementText(String name, String phoneNumber, String propertyName, String propertyPhone,
        String propertyEmail, String propertyManager) {
}
