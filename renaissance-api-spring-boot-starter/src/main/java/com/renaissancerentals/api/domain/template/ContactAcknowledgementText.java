package com.renaissancerentals.api.domain.template;

import com.renaissancerentals.api.util.StringUtils;
import lombok.Builder;

@Builder
public record ContactAcknowledgementText(
        String firstName,
        String lastName,
        String phoneNumber,
        String propertyName,
        String propertyPhone,
        String propertyEmail,
        String propertyManager) {
    @Override
    public String firstName() {
        return StringUtils.capitalizeWords(firstName);
    }

    @Override
    public String lastName() {
        return StringUtils.capitalizeWords(lastName);
    }

    @Override
    public String propertyName() {
        return StringUtils.capitalizeWords(propertyName);
    }

    @Override
    public String propertyManager() {
        return StringUtils.capitalizeWords(StringUtils.getFirstName(propertyManager));
    }
}
