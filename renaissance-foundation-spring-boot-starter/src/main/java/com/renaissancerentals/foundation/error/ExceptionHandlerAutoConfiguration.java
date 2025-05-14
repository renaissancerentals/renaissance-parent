package com.renaissancerentals.foundation.error;

import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import com.renaissancerentals.foundation.error.notification.component.ExceptionNotifier;
import com.renaissancerentals.foundation.error.notification.config.ErrorNotificationConfig;
import com.renaissancerentals.foundation.error.notification.config.ErrorNotificationConfigProperties;
import com.renaissancerentals.foundation.error.notification.mail.ServerErrorMailTemplate;

@Configuration
@EnableConfigurationProperties(ErrorNotificationConfigProperties.class)
@ImportAutoConfiguration(classes = {GlobalExceptionHandler.class, ServerErrorMailTemplate.class,
        ExceptionNotifier.class, ErrorNotificationConfig.class})
public class ExceptionHandlerAutoConfiguration {
}
