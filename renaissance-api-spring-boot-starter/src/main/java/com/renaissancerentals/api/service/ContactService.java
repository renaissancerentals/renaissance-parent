package com.renaissancerentals.api.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.renaissancerentals.api.error.NotFoundException;
import com.renaissancerentals.api.messaging.ContactMessageRequest;
import com.renaissancerentals.api.repository.ContactRepository;
import com.renaissancerentals.foundation.mail.model.MailMessage;
import com.renaissancerentals.foundation.mail.service.MailService;
import com.renaissancerentals.foundation.mail.template.MailMessageFactory;
import com.renaissancerentals.persistence.dao.PropertyDao;
import com.renaissancerentals.persistence.entity.PropertyEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    private final PropertyDao propertyRepository;

    private final MailService mailService;
    private final MailMessageFactory mailMessageFactory;
    @Value("${renaissancerentals.mail.cc}")
    private final List<String> cc;

    @Transactional
    public void save(ContactMessageRequest contactMessage){

        contactRepository.save(contactMessage);

        var property = propertyRepository.findById(contactMessage.property())
                .orElseThrow(() -> new NotFoundException("Property not found"));
        sendContactEmail(contactMessage,property);

    }

    private List<String> getCC(PropertyEntity property){

        List<String> ccList = new ArrayList<>(cc);
        if (property.getSecondaryEmail() != null && !property.getSecondaryEmail().isEmpty()) {
            ccList.add(property.getSecondaryEmail());
        }

        return ccList;

    }

    private void sendContactEmail(final ContactMessageRequest contactMessage,final PropertyEntity property){
        var message = mailMessageFactory.createMessage(contactMessage);
        var subject = String.format("Message from %s by %s",property.getName(),contactMessage.name());
        mailService.sendMail(MailMessage.builder().subject(subject).replyTo(contactMessage.email())
                .to(getEmailTo(property)).cc(getCC(property)).build(),message);
    }

    private String getEmailTo(final PropertyEntity property){

        return Optional.ofNullable(property.getEmail()).orElse("inquiries@renaissancerentals.com");

    }

}
