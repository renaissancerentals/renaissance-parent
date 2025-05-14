package com.renaissancerentals.foundation.mail.error;

import com.renaissancerentals.foundation.error.ErrorMessage;
import com.renaissancerentals.foundation.error.ServerException;

import lombok.Getter;

@Getter
public class MailServerException extends ServerException {
    private final MailErrorCode errorCode;

    public MailServerException(MailErrorCode errorCode, Throwable cause) {
        super(ErrorMessage.builder().code(errorCode.name()).message(errorCode.message()).build(), cause);
        this.errorCode = errorCode;
    }
}
