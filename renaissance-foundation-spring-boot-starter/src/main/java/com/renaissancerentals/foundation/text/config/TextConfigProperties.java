package com.renaissancerentals.foundation.text.config;

import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;

import com.renaissancerentals.foundation.text.data.TextExtension;

@ConfigurationProperties(prefix = "renaissancerentals.text")
public record TextConfigProperties(boolean enabled, String serverUrl, Map<String, TextExtension> extensions) {
    public TextConfigProperties {
        extensions = extensions == null ? Map.of() : Map.copyOf(extensions);
    }
}
