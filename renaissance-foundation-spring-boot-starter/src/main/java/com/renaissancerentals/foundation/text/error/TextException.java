package com.renaissancerentals.foundation.text.error;

import com.renaissancerentals.foundation.error.ErrorMessage;
import com.renaissancerentals.foundation.error.ServerException;
import org.springframework.http.HttpStatus;

public class TextException extends ServerException {
    public TextException(Throwable cause) {
        super(
                ErrorMessage.builder()
                        .message("Error Sending Text Message")
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .build(),
                cause);
    }
}
