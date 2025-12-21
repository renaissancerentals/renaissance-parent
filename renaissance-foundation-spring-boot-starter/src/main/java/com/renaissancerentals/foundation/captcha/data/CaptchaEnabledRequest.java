package com.renaissancerentals.foundation.captcha.data;

import com.renaissancerentals.foundation.captcha.validation.HoneyPot;

import lombok.Data;

/**
 * Use it with @valid annotation.
 */
@Data
public abstract class CaptchaEnabledRequest {
    @HoneyPot
    private String preferredName;// aka if preferredName is the honeypot here, if provided, bot activity detected
}
