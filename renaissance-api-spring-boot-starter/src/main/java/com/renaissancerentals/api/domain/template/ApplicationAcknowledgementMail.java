package com.renaissancerentals.api.domain.template;

import lombok.Builder;

@Builder
public record ApplicationAcknowledgementMail(String email, String name, String propertyName, String propertyPhone,
        String propertyManager, String propertyEmail) {
}
