package com.renaissancerentals.api.messaging;

import com.renaissancerentals.foundation.captcha.data.CaptchaEnabledRequest;
import com.renaissancerentals.foundation.captcha.validation.HoneyPot;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public record ContactMessageRequest(@NotNull String firstName, @NotNull String lastName, @NotNull @Email String email,

        @NotNull String phone, String question,

        @NotNull String property, @NotNull String currentPage,

        boolean phonePreferred, boolean textPreferred, boolean emailPreferred,

        ContactAdditionalInfoRequest additionalInfo,

        @HoneyPot String preferredName) implements CaptchaEnabledRequest {
    public ContactMessageRequest {
        additionalInfo = additionalInfo == null ? null : new ContactAdditionalInfoRequest(additionalInfo);
    }
}
