package com.renaissancerentals.monitor.model;

import java.time.LocalDateTime;

import lombok.Builder;

@Builder
public record HealthMetadata(HealthStatus status, LocalDateTime updatedAt, int retryCounter, boolean notificationSent,
        String errorMessage, String errorType, LocalDateTime errorSince) {
}
