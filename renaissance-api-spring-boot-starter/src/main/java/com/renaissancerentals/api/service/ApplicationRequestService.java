package com.renaissancerentals.api.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.renaissancerentals.api.domain.mapper.ApplicationRequestMapper;
import com.renaissancerentals.api.error.NotFoundException;
import com.renaissancerentals.api.messaging.ApplicationRequest;
import com.renaissancerentals.foundation.mail.model.MailMessage;
import com.renaissancerentals.foundation.mail.service.MailService;
import com.renaissancerentals.foundation.mail.template.MailMessageFactory;
import com.renaissancerentals.persistence.dao.ApplicationEmailDao;
import com.renaissancerentals.persistence.dao.PropertyDao;
import com.renaissancerentals.persistence.entity.PropertyEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationRequestService {

    private final ApplicationEmailDao applicationEmailRepository;
    private final ApplicationRequestMapper applicationRequestMapper;

    private final PropertyDao propertyRepository;

    private final MailService mailService;
    private final MailMessageFactory mailMessageFactory;
    @Value("${renaissancerentals.mail.cc}")
    private final List<String> cc;

    @Transactional
    public void save(ApplicationRequest applicationRequest){

        var applicationEmail = applicationRequestMapper.toEntity(applicationRequest);
        applicationEmail.setId(UUID.randomUUID());
        applicationEmail.setCreatedAt(Instant.now());
        applicationEmail.markNew();
        applicationEmailRepository.save(applicationEmail);

        var property = propertyRepository.findById(applicationRequest.property())
                .orElseThrow(() -> new NotFoundException("Property not found"));
        sendApplicationRequestEmail(applicationRequest,property);

    }

    private List<String> getCC(PropertyEntity property){

        List<String> ccList = new ArrayList<>(cc);
        if (property.getSecondaryEmail() != null && !property.getSecondaryEmail().isEmpty()) {
            ccList.add(property.getSecondaryEmail());
        }

        return ccList;

    }

    private void sendApplicationRequestEmail(final ApplicationRequest applicationRequest,final PropertyEntity property){
        var message = mailMessageFactory.createMessage(applicationRequest);
        var subject = String.format("Rental Application Request from %s by %s",property.getName(),
                applicationRequest.name());
        mailService.sendMail(MailMessage.builder().subject(subject).replyTo(applicationRequest.email())
                .to(getEmailTo(property)).cc(getCC(property)).build(),message);
    }

    private String getEmailTo(final PropertyEntity property){

        return Optional.ofNullable(property.getEmail()).orElse("inquiries@renaissancerentals.com");

    }

}
