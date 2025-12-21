package com.renaissancerentals.foundation.captcha.error;

import org.springframework.http.HttpStatus;

import com.renaissancerentals.foundation.error.ClientException;
import com.renaissancerentals.foundation.error.ErrorMessage;

public class CaptchaException extends ClientException {
    public CaptchaException() {
        super(ErrorMessage.builder().message("Captcha error").status(HttpStatus.UNPROCESSABLE_ENTITY).build());
    }
}
