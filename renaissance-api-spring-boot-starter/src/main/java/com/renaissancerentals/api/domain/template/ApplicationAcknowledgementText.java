package com.renaissancerentals.api.domain.template;

import lombok.Builder;

@Builder
public record ApplicationAcknowledgementText(String name, String phoneNumber, String propertyName, String propertyEmail,
        String propertyPhone, String propertyManager) {
}
