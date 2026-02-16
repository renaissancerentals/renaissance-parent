package com.renaissancerentals.api.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.renaissancerentals.api.domain.Sublet;
import com.renaissancerentals.api.domain.mail.SubletEmailMessage;
import com.renaissancerentals.api.messaging.SubletMessageRequest;
import com.renaissancerentals.foundation.mail.model.MailMessage;
import com.renaissancerentals.foundation.mail.service.MailService;
import com.renaissancerentals.foundation.mail.template.MailMessageFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubletMessageService {

    private final MailService mailService;
    private final MailMessageFactory mailMessageFactory;

    @Value("${renaissancerentals.sublet.alertTo}")
    private String alertTo;

    public void sendMessage(Sublet sublet,SubletMessageRequest subletMessage){

        var message = mailMessageFactory.createMessage(SubletEmailMessage.builder().subletTitle(sublet.title())
                .ownerName(sublet.firstName()).messenger(subletMessage.name()).messengerEmail(subletMessage.email())
                .message(subletMessage.message()).build());

        mailService.sendMail(MailMessage.builder().to(sublet.email()).replyTo(subletMessage.email())
                .subject(String.format("Message for your sublet - %s ",sublet.title())).build(),message);
    }

    public void sendNewSubletAlert(Sublet sublet){

        var message = mailMessageFactory.createMessage(sublet);

        mailService.sendMail(MailMessage.builder().to(alertTo).replyTo(sublet.email())
                .subject(String.format("New Sublet Posted: - %s ",sublet.title())).build(),message);
    }
}
