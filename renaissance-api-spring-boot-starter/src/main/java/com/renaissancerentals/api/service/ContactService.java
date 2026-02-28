package com.renaissancerentals.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.renaissancerentals.api.domain.PropertyContact;
import com.renaissancerentals.api.domain.template.ContactAcknowledgementMail;
import com.renaissancerentals.api.domain.template.ContactAcknowledgementText;
import com.renaissancerentals.api.messaging.ContactMessageRequest;
import com.renaissancerentals.api.repository.ContactRepository;
import com.renaissancerentals.foundation.mail.model.MailMessage;
import com.renaissancerentals.foundation.mail.service.MailService;
import com.renaissancerentals.foundation.template.TemplateMessageFactory;
import com.renaissancerentals.foundation.text.data.TextMessage;
import com.renaissancerentals.foundation.text.service.TextService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    private final PropertyService propertyService;

    private final MailService mailService;
    private final TextService textService;
    private final TemplateMessageFactory templateMessageFactory;
    @Value("${renaissancerentals.mail.cc}")
    private final List<String> cc;

    @Transactional
    public void save(ContactMessageRequest contactMessage){

        contactRepository.save(contactMessage);

        var property = propertyService.getPropertyContact(contactMessage.property());

        sendContactEmail(contactMessage,property);
        var propertyManager = propertyService.getPropertyManager(contactMessage.property());
        sendContactAcknowledgementMail(ContactAcknowledgementMail.builder().name(contactMessage.name())
                .email(contactMessage.email()).propertyName(property.propertyName()).propertyPhone(property.phone())
                .propertyEmail(property.email()).propertyManager(propertyManager.getName())
                .propertyUrl(propertyService.getPropertyUrl(contactMessage.property())).build());

        if (contactMessage.phone() != null) {
            sendContactAcknowledgementText(
                    ContactAcknowledgementText.builder().name(contactMessage.name()).phoneNumber(contactMessage.phone())
                            .propertyName(property.propertyName()).propertyPhone(property.phone())
                            .propertyEmail(property.email()).propertyManager(propertyManager.getName()).build());
        }

    }

    private List<String> getCC(String secondaryEmail){

        List<String> ccList = new ArrayList<>(cc);
        if (secondaryEmail != null && !secondaryEmail.isEmpty()) {
            ccList.add(secondaryEmail);
        }

        return ccList;

    }

    private void sendContactEmail(final ContactMessageRequest contactMessage,final PropertyContact property){
        var message = templateMessageFactory.createMessage(contactMessage);
        var subject = String.format("Message from %s by %s",property.propertyName(),contactMessage.name());
        mailService.sendMail(MailMessage.builder().subject(subject).replyTo(contactMessage.email())
                .to(getEmailTo(property.email())).cc(getCC(property.secondaryEmail())).build(),message);
    }

    private void sendContactAcknowledgementMail(final ContactAcknowledgementMail acknowledgementMail){
        var message = templateMessageFactory.createMessage(acknowledgementMail);
        var subject = String.format("Your contact form has been received! - %s",acknowledgementMail.propertyName());
        mailService.sendHtmlMail(MailMessage.builder().subject(subject).replyTo(acknowledgementMail.propertyEmail())
                .to(acknowledgementMail.email()).build(),message);
    }

    private void sendContactAcknowledgementText(final ContactAcknowledgementText acknowledgementText){
        var message = templateMessageFactory.createMessage(acknowledgementText);
        textService.sendText(TextMessage.builder().from(acknowledgementText.propertyPhone())
                .to(acknowledgementText.phoneNumber()).message(message).build());
    }

    private String getEmailTo(final String propertyEmail){

        return Optional.ofNullable(propertyEmail).orElse("inquiries@renaissancerentals.com");

    }

}
