package com.renaissancerentals.api.messaging;

import com.renaissancerentals.foundation.captcha.data.CaptchaEnabledRequest;
import com.renaissancerentals.foundation.captcha.validation.HoneyPot;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record UtilityConfirmationRequest(@NotNull String name,

        @NotNull @Email String email,

        @NotNull String unitId,

        @NotNull String unitAddress,

        @NotNull String emailTo,

        String vectrenAccountNumber, String dukeAccountNumber, boolean waterFormSubmitted,

        @HoneyPot String preferredName) implements CaptchaEnabledRequest {

}
