package com.renaissancerentals.api.domain.template;

import lombok.Builder;

@Builder
public record ContactAcknowledgementMail(String name, String email, String propertyName, String propertyPhone,
        String propertyEmail, String propertyManager, String propertyUrl) {
    @Override
    public String name(){
        return name != null ? name.toUpperCase() : null;
    }

    @Override
    public String propertyName(){
        return propertyName;
    }

    @Override
    public String propertyManager(){
        return propertyManager;
    }
}
