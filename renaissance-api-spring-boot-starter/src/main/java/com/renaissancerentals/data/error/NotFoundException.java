package com.renaissancerentals.data.error;

import org.springframework.http.HttpStatus;

import com.renaissancerentals.foundation.error.BusinessException;
import com.renaissancerentals.foundation.error.ErrorMessage;

import lombok.Getter;

@Getter
public class NotFoundException extends BusinessException {

    public NotFoundException(String message) {
        super(ErrorMessage.builder().code(ErrorCode.NOT_FOUND_ERROR.name())
                .message(ErrorCode.NOT_FOUND_ERROR.format(message)).status(HttpStatus.NOT_FOUND).build());

    }
}
