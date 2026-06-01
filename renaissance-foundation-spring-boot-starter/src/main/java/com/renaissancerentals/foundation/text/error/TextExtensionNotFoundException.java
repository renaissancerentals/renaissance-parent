package com.renaissancerentals.foundation.text.error;

import com.renaissancerentals.foundation.error.ClientException;
import com.renaissancerentals.foundation.error.ErrorMessage;
import org.springframework.http.HttpStatus;

public class TextExtensionNotFoundException extends ClientException {
    public TextExtensionNotFoundException(String fromNumber) {
        super(ErrorMessage.builder()
                .message(String.format("extension number %s not found", fromNumber))
                .status(HttpStatus.NOT_FOUND)
                .build());
    }
}
