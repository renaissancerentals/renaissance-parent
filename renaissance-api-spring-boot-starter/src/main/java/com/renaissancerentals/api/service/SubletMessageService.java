package com.renaissancerentals.api.service;

import com.renaissancerentals.api.domain.Sublet;
import com.renaissancerentals.api.domain.template.SubletEmailMessage;
import com.renaissancerentals.api.messaging.SubletMessageRequest;
import com.renaissancerentals.foundation.mail.model.MailMessage;
import com.renaissancerentals.foundation.mail.service.MailService;
import com.renaissancerentals.foundation.template.TemplateMessageFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubletMessageService {

    private final MailService mailService;
    private final TemplateMessageFactory templateMessageFactory;

    @Value("${renaissancerentals.sublet.alertTo}")
    private String alertTo;

    public void sendMessage(Sublet sublet, SubletMessageRequest subletMessage) {

        var message = templateMessageFactory.createMessage(SubletEmailMessage.builder()
                .subletTitle(sublet.title())
                .ownerName(sublet.firstName())
                .messenger(subletMessage.name())
                .messengerEmail(subletMessage.email())
                .message(subletMessage.message())
                .build());

        mailService.sendMail(
                MailMessage.builder()
                        .to(sublet.email())
                        .replyTo(subletMessage.email())
                        .subject(String.format("Message for your sublet - %s ", sublet.title()))
                        .build(),
                message);
    }

    public void sendNewSubletAlert(Sublet sublet) {

        var message = templateMessageFactory.createMessage(sublet);

        mailService.sendMail(
                MailMessage.builder()
                        .to(alertTo)
                        .replyTo(sublet.email())
                        .subject(String.format("New Sublet Posted: - %s ", sublet.title()))
                        .build(),
                message);
    }
}
