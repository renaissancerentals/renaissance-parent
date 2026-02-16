package com.renaissancerentals.foundation.captcha.data;

public interface CaptchaEnabledRequest {
    String preferredName();// aka if preferredName is the honeypot here, if provided, bot activity detected
}
