package com.renaissancerentals.monitor.template.model;

import com.renaissancerentals.monitor.model.HealthStatus;

import lombok.Builder;

@Builder
public record MonitorErrorMessage(String applicationName, String time, String healthEndpoint, HealthStatus status,
        String errorType, String errorSince, int retryCount, String errorMessage) {
}
