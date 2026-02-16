package com.renaissancerentals.api.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.renaissancerentals.api.messaging.UtilityConfirmationRequest;
import com.renaissancerentals.foundation.mail.model.MailMessage;
import com.renaissancerentals.foundation.mail.service.MailService;
import com.renaissancerentals.foundation.mail.template.MailMessageFactory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UtilityConfirmationService {

    private final MailService mailService;
    private final MailMessageFactory mailMessageFactory;
    @Value("${renaissancerentals.mail.cc}")
    private final List<String> cc;

    public void save(UtilityConfirmationRequest utilityConfirmation){
        var message = mailMessageFactory.createMessage(utilityConfirmation);
        var subject = String.format("Utility Setup Confirmation by by %s",utilityConfirmation.name());
        mailService.sendMail(MailMessage.builder().subject(subject).replyTo(utilityConfirmation.email())
                .to(utilityConfirmation.emailTo()).cc(cc).build(),message);

    }

}
