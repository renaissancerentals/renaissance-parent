package com.renaissancerentals.monitor.model;

import lombok.Builder;

@Builder
public record MonitoringApp(String name, String healthEndpoint, HealthMetadata health) {
}
