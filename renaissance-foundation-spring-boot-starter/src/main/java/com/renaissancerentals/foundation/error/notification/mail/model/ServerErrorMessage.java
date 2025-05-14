package com.renaissancerentals.foundation.error.notification.mail.model;

import lombok.Builder;

@Builder
public record ServerErrorMessage(String errorCode, String sourceName, String timeStamp, String exceptionClass,
        String message, String stackTrace) {
}
