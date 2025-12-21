package com.renaissancerentals.foundation.captcha.validation;

import com.renaissancerentals.foundation.captcha.error.CaptchaException;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class HoneyPotValidator implements ConstraintValidator<HoneyPot, String> {
    @Override
    public boolean isValid(String value,ConstraintValidatorContext context){
        if (value != null && !value.isEmpty())
            throw new CaptchaException();
        return true;
    }
}
