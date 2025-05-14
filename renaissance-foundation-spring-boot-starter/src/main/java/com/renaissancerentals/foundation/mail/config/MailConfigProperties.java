package com.renaissancerentals.foundation.mail.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "renaissancerentals.mail")
public record MailConfigProperties(boolean enabled, String clientId, String clientSecret, String refreshToken,
        String tokenServer, String from, String cc, String fromName) {
}
