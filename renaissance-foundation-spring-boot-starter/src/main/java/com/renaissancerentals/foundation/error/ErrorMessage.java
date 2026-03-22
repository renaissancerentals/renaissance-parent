package com.renaissancerentals.foundation.error;

import org.springframework.http.HttpStatus;

import lombok.Builder;

@Builder
public record ErrorMessage(String message, String code, HttpStatus status, String application) {
}
