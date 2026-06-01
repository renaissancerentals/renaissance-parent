package com.renaissancerentals.foundation.mail.error;

import com.renaissancerentals.foundation.error.BusinessException;
import com.renaissancerentals.foundation.error.ErrorMessage;
import lombok.Getter;

@Getter
public class MailBusinessException extends BusinessException {
    private final MailErrorCode errorCode;

    public MailBusinessException(MailErrorCode errorCode, Throwable cause) {
        super(
                ErrorMessage.builder()
                        .code(errorCode.name())
                        .message(errorCode.message())
                        .build(),
                cause);
        this.errorCode = errorCode;
    }
}
