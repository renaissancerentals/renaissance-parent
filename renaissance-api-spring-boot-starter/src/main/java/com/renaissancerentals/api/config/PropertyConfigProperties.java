package com.renaissancerentals.api.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "renaissancerentals.property")
public record PropertyConfigProperties(String defaultPropertyEmail, String defaultPropertyPhone,
        String defaultPropertyManager, String defaultPropertyUrl, Map<String, String> propertyUrls,
        Map<String, String> propertyNameIds) {
    public PropertyConfigProperties {
        propertyUrls = propertyUrls == null ? Map.of() : Map.copyOf(propertyUrls);
        propertyNameIds = propertyNameIds == null ? Map.of() : Map.copyOf(propertyNameIds);
    }
}
