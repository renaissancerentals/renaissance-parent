package com.renaissancerentals.monitor.service;

import org.springframework.stereotype.Service;

import com.renaissancerentals.mail.model.MailMessage;
import com.renaissancerentals.mail.service.MailService;
import com.renaissancerentals.mail.template.MailMessageFactory;
import com.renaissancerentals.monitor.config.MonitorConfigProperties;
import com.renaissancerentals.monitor.template.model.MonitorErrorMessage;
import com.renaissancerentals.monitor.template.model.MonitorHealthyMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MonitorMailService {
    private final MailService mailService;
    private final MailMessageFactory mailMessageFactory;
    private final MonitorConfigProperties configProperties;

    public void sendErrorMail(MonitorErrorMessage errorMessage){
        var message = mailMessageFactory.createMessage(errorMessage);
        mailService.sendMail(MailMessage.builder().to(configProperties.sendNotificationEmailTo())
                .subject(String.format("%s is %s (Retry %d)",errorMessage.applicationName(),errorMessage.status(),
                        errorMessage.retryCount()))
                .build(),message);
    }

    public void sendHealthyMail(MonitorHealthyMessage healthyMessage){
        var message = mailMessageFactory.createMessage(healthyMessage);
        mailService.sendMail(MailMessage.builder().to(configProperties.sendNotificationEmailTo())
                .subject(String.format("%s is Restored now",healthyMessage.applicationName())).build(),message);
    }

}
