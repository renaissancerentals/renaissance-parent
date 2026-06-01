package com.renaissancerentals.monitor.service;

import com.renaissancerentals.foundation.mail.model.MailMessage;
import com.renaissancerentals.foundation.mail.service.MailService;
import com.renaissancerentals.foundation.template.TemplateMessageFactory;
import com.renaissancerentals.monitor.config.MonitorConfigProperties;
import com.renaissancerentals.monitor.template.model.MonitorErrorMessage;
import com.renaissancerentals.monitor.template.model.MonitorHealthyMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MonitorMailService {
    private final MailService mailService;
    private final TemplateMessageFactory templateMessageFactory;
    private final MonitorConfigProperties configProperties;

    public void sendErrorMail(MonitorErrorMessage errorMessage) {
        var message = templateMessageFactory.createMessage(errorMessage);
        mailService.sendMail(
                MailMessage.builder()
                        .to(configProperties.sendNotificationEmailTo())
                        .subject(String.format(
                                "%s is %s (Retry %d)",
                                errorMessage.applicationName(), errorMessage.status(), errorMessage.retryCount()))
                        .build(),
                message);
    }

    public void sendHealthyMail(MonitorHealthyMessage healthyMessage) {
        var message = templateMessageFactory.createMessage(healthyMessage);
        mailService.sendMail(
                MailMessage.builder()
                        .to(configProperties.sendNotificationEmailTo())
                        .subject(String.format("%s is Restored now", healthyMessage.applicationName()))
                        .build(),
                message);
    }
}
