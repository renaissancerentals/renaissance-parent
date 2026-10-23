package com.renaissancerentals.api.error;

import com.renaissancerentals.foundation.error.ClientException;
import com.renaissancerentals.foundation.error.ErrorMessage;
import org.springframework.http.HttpStatus;

/** The request itself is not acceptable, e.g. it names a property that does not exist. */
public class BadRequestException extends ClientException {
    public BadRequestException(String message) {
        super(ErrorMessage.builder()
                .message(message)
                .status(HttpStatus.BAD_REQUEST)
                .build());
    }
}
