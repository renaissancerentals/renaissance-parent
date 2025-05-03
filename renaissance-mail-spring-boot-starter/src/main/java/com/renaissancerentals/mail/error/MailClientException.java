package com.renaissancerentals.mail.error;

import com.renaissancerentals.error.ClientException;
import com.renaissancerentals.error.ErrorMessage;

public class MailClientException extends ClientException {
    public MailClientException(MailErrorCode errorCode, Throwable cause) {
        super(ErrorMessage.builder().code(errorCode.name()).message(errorCode.message()).build(), cause);
    }
}
