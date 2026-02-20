package com.renaissancerentals.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "renaissancerentals.text")
public record TextConfigProperties(boolean enabled, String clientId, String clientSecret, String serverUrl,
                                   String jwtToken) {
}
