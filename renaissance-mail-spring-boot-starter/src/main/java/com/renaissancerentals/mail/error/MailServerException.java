package com.renaissancerentals.mail.error;

import com.renaissancerentals.error.ErrorMessage;
import com.renaissancerentals.error.ServerException;

import lombok.Getter;

@Getter
public class MailServerException extends ServerException {
    private final MailErrorCode errorCode;

    public MailServerException(MailErrorCode errorCode, Throwable cause) {
        super(ErrorMessage.builder().code(errorCode.name()).message(errorCode.message()).build(), cause);
        this.errorCode = errorCode;
    }
}
