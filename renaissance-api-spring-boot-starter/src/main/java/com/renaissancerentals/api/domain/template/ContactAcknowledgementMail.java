package com.renaissancerentals.api.domain.template;

import com.renaissancerentals.api.util.StringUtils;

import lombok.Builder;

@Builder
public record ContactAcknowledgementMail(String name, String email, String propertyName, String propertyPhone,
        String propertyEmail, String propertyManager, String propertyUrl) {
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
