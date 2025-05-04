package com.renaissancerentals.monitor.template.model;

import com.renaissancerentals.monitor.model.HealthStatus;

import lombok.Builder;

@Builder
public record MonitorHealthyMessage(String applicationName, String time, String healthEndpoint,
        HealthStatus previousStatus, String downtimeDuration) {
}
