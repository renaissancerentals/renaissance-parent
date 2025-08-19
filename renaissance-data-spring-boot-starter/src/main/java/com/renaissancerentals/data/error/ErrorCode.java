package com.renaissancerentals.data.error;

import java.text.MessageFormat;

public enum ErrorCode {
    NOT_FOUND_ERROR("{0} not found.");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }

    public String format(Object... args) {
        return MessageFormat.format(message, args);
    }
}
