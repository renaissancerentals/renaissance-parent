package com.renaissancerentals.foundation.error.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.renaissancerentals.foundation.error.ServerException;
import com.renaissancerentals.foundation.error.notification.component.ExceptionNotifier;
import com.renaissancerentals.foundation.mail.service.MailService;
import com.renaissancerentals.foundation.ratelimiter.RateLimiter;
import com.renaissancerentals.foundation.template.TemplateMessageFactory;

@Configuration
public class ErrorNotificationConfig {

    @Bean
    public ExceptionNotifier<ServerException> serverExceptionNotifier(ErrorNotificationConfigProperties properties,
            TemplateMessageFactory templateMessageFactory,MailService mailService,RateLimiter rateLimiter){
        return new ExceptionNotifier<>(properties, templateMessageFactory, mailService, rateLimiter);
    }
}
