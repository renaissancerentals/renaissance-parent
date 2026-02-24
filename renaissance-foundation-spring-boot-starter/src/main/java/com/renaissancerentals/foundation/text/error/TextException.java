package com.renaissancerentals.foundation.text.error;

import org.springframework.http.HttpStatus;

import com.renaissancerentals.foundation.error.ErrorMessage;
import com.renaissancerentals.foundation.error.ServerException;

public class TextException extends ServerException {
    public TextException(Throwable cause) {
        super(ErrorMessage.builder().message("Error Sending Text Message").status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build(), cause);
    }
}
