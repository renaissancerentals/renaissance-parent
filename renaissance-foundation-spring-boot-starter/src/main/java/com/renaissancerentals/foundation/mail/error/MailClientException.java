package com.renaissancerentals.foundation.mail.error;

import com.renaissancerentals.foundation.error.ClientException;
import com.renaissancerentals.foundation.error.ErrorMessage;

public class MailClientException extends ClientException {
    public MailClientException(MailErrorCode errorCode, Throwable cause) {
        super(
                ErrorMessage.builder()
                        .code(errorCode.name())
                        .message(errorCode.message())
                        .build(),
                cause);
    }
}
