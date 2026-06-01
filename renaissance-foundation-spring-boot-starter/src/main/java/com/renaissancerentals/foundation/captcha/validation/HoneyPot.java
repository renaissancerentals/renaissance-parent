package com.renaissancerentals.foundation.captcha.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = HoneyPotValidator.class)
public @interface HoneyPot {
    String message() default "Bot activity detected";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
