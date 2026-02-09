package com.renaissancerentals.foundation.captcha.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class HoneyPotValidator implements ConstraintValidator<HoneyPot, String> {
    @Override
    public boolean isValid(String value,ConstraintValidatorContext context){
        return value == null || value.isEmpty();
    }
}
