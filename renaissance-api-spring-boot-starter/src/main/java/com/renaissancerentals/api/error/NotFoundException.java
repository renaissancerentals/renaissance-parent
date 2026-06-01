package com.renaissancerentals.api.error;

import com.renaissancerentals.foundation.error.ClientException;
import com.renaissancerentals.foundation.error.ErrorMessage;
import org.springframework.http.HttpStatus;

public class NotFoundException extends ClientException {
    public NotFoundException(String message) {
        super(ErrorMessage.builder()
                .message(message)
                .status(HttpStatus.NOT_FOUND)
                .build());
    }
}
