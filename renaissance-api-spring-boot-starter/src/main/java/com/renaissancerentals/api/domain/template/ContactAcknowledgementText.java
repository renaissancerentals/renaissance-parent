package com.renaissancerentals.api.domain.template;

import com.renaissancerentals.api.util.StringUtils;

import lombok.Builder;

@Builder
public record ContactAcknowledgementText(String name, String phoneNumber, String propertyName, String propertyPhone,
        String propertyEmail, String propertyManager) {
    @Override
    public String name(){
        return StringUtils.capitalizeWords(name);
    }

    @Override
    public String propertyName(){
        return StringUtils.capitalizeWords(propertyName);
    }

    @Override
    public String propertyManager(){
        return StringUtils.capitalizeWords(StringUtils.getFirstName(propertyManager));
    }
}
