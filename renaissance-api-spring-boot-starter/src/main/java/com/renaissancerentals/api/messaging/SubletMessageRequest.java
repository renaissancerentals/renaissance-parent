package com.renaissancerentals.api.messaging;

import com.renaissancerentals.foundation.captcha.data.CaptchaEnabledRequest;
import com.renaissancerentals.foundation.captcha.validation.HoneyPot;
import jakarta.validation.constraints.NotNull;

public record SubletMessageRequest(
        @NotNull String message, @NotNull String name, @NotNull String email, @HoneyPot String preferredName)
        implements CaptchaEnabledRequest {}
