package com.renaissancerentals.api.messaging;

import com.renaissancerentals.foundation.captcha.data.CaptchaEnabledRequest;
import com.renaissancerentals.foundation.captcha.validation.HoneyPot;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record SubletRequest(
        @NotNull String firstName,
        @NotNull String lastName,
        @NotNull String email,
        @NotNull Integer bedroom,
        @NotNull Integer availableBedrooms,
        @NotNull LocalDate availableFrom,
        @NotNull LocalDate availableTo,
        @NotNull Float rent,
        @NotNull Boolean petsAllowed,
        @NotNull Boolean utilitiesIncluded,
        @Size(max = 255, message = "allowed length up to {max}") @NotNull String address,
        @NotNull String zipcode,
        @NotNull @Size(max = 255, message = "allowed length up to {max}") String title,
        @NotNull String description,
        @HoneyPot String preferredName)
        implements CaptchaEnabledRequest {}
