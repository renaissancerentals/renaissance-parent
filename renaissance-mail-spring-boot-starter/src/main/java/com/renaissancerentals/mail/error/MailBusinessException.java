package com.renaissancerentals.mail.error;

import com.renaissancerentals.error.BusinessException;
import com.renaissancerentals.error.ErrorMessage;

import lombok.Getter;

@Getter
public class MailBusinessException extends BusinessException {
    private final MailErrorCode errorCode;

    public MailBusinessException(MailErrorCode errorCode, Throwable cause) {
        super(ErrorMessage.builder().code(errorCode.name()).message(errorCode.message()).build(), cause);
        this.errorCode = errorCode;
    }

}
