package com.renaissancerentals.api.messaging;

import com.renaissancerentals.foundation.captcha.data.CaptchaEnabledRequest;
import com.renaissancerentals.foundation.captcha.validation.HoneyPot;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record ApplicationRequest(@NotNull String firstName, @NotNull String lastName,

        @NotNull @Email String email,

        String phone,

        @NotNull String property,

        @NotNull String currentPage,

        @NotNull String community,

        String address,

        String questions,

        @HoneyPot String preferredName) implements CaptchaEnabledRequest {
}
