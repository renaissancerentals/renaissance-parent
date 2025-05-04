package com.renaissancerentals.assets.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "renaissancerentals.assets")
public record AssetsConfigProperties(boolean enabled, String clientId, String clientSecret, String refreshToken,
        String tokenServer, String defaultFolder) {
}
