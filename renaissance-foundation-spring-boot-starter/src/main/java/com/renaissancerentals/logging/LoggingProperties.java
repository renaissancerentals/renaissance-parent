package com.renaissancerentals.logging;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "renaissancerentals.logging")
public record LoggingProperties(boolean enabled) {
}
