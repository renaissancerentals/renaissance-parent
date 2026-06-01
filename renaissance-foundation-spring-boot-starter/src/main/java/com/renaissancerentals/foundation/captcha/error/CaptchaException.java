package com.renaissancerentals.foundation.captcha.error;

import com.renaissancerentals.foundation.error.ClientException;
import com.renaissancerentals.foundation.error.ErrorMessage;
import org.springframework.http.HttpStatus;

public class CaptchaException extends ClientException {
    public CaptchaException() {
        super(ErrorMessage.builder()
                .message("Captcha error")
                .status(HttpStatus.UNPROCESSABLE_ENTITY)
                .build());
    }
}
