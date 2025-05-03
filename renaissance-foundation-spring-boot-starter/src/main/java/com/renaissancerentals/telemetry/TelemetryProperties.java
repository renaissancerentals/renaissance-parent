package com.renaissancerentals.telemetry;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "renaissancerentals.telemetry")
public record TelemetryProperties(boolean enabled) {
}
