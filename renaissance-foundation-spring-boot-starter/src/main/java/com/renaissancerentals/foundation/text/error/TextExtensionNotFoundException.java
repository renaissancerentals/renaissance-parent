package com.renaissancerentals.foundation.text.error;

import org.springframework.http.HttpStatus;

import com.renaissancerentals.foundation.error.ClientException;
import com.renaissancerentals.foundation.error.ErrorMessage;

public class TextExtensionNotFoundException extends ClientException {
    public TextExtensionNotFoundException(String fromNumber) {
        super(ErrorMessage.builder().message(String.format("extension number %s not found",fromNumber))
                .status(HttpStatus.NOT_FOUND).build());
    }
}
