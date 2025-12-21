package com.renaissancerentals.foundation.captcha.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = HoneyPotValidator.class)
public @interface HoneyPot {
    String message() default "Bot activity detected";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
