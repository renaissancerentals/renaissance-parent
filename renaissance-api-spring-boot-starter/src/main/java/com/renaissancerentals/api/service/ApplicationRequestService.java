package com.renaissancerentals.api.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.renaissancerentals.api.domain.TeamMember;
import com.renaissancerentals.api.domain.mapper.ApplicationRequestMapper;
import com.renaissancerentals.api.domain.projection.PropertyContact;
import com.renaissancerentals.api.domain.template.ApplicationAcknowledgementMail;
import com.renaissancerentals.api.domain.template.ApplicationAcknowledgementText;
import com.renaissancerentals.api.messaging.ApplicationRequest;
import com.renaissancerentals.foundation.mail.model.MailMessage;
import com.renaissancerentals.foundation.mail.service.MailService;
import com.renaissancerentals.foundation.template.TemplateMessageFactory;
import com.renaissancerentals.foundation.text.data.TextMessage;
import com.renaissancerentals.foundation.text.service.TextService;
import com.renaissancerentals.persistence.dao.ApplicationEmailDao;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApplicationRequestService {

    private final ApplicationEmailDao applicationEmailRepository;
    private final ApplicationRequestMapper applicationRequestMapper;

    private final PropertyService propertyService;

    private final MailService mailService;
    private final TextService textService;
    private final TemplateMessageFactory templateMessageFactory;
    private final ExecutorService virtualThreadExecutor;

    @Value("${renaissancerentals.mail.cc}")
    private final List<String> cc;

    @Transactional
    public void save(ApplicationRequest applicationRequest){

        var applicationEmail = applicationRequestMapper.toEntity(applicationRequest);
        applicationEmail.setId(UUID.randomUUID());
        applicationEmail.setCreatedAt(Instant.now());
        applicationEmail.markNew();
        applicationEmailRepository.save(applicationEmail);

        var property = propertyService.getPropertyContact(applicationRequest.property());
        sendApplicationRequestEmail(applicationRequest,property);

        virtualThreadExecutor.execute(() -> {
            var propertyManager = propertyService.getPropertyManager(applicationRequest.property());
            sendAcknowledgements(applicationRequest,property,propertyManager);
        });
    }

    private void sendAcknowledgements(ApplicationRequest applicationRequest,PropertyContact property,
            TeamMember propertyManager){
        sendApplicationRequestAcknowledgementMail(ApplicationAcknowledgementMail.builder()
                .firstName(applicationRequest.firstName()).lastName(applicationRequest.lastName())
                .email(applicationRequest.email()).propertyName(property.propertyName()).propertyPhone(property.phone())
                .propertyEmail(property.email()).propertyManager(propertyManager.getName()).build());
        if (applicationRequest.phone() != null) {
            sendApplicationRequestAcknowledgementText(
                    ApplicationAcknowledgementText.builder().firstName(applicationRequest.firstName())
                            .lastName(applicationRequest.lastName()).phoneNumber(applicationRequest.phone())
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

    private void sendApplicationRequestEmail(final ApplicationRequest applicationRequest,
            final PropertyContact property){
        var message = templateMessageFactory.createMessage(applicationRequest);
        var subject = String.format("Rental Application Request from %s by %s",property.propertyName(),
                applicationRequest.firstName());
        mailService.sendMail(MailMessage.builder().subject(subject).replyTo(applicationRequest.email())
                .to(getEmailTo(property.email())).cc(getCC(property.secondaryEmail())).build(),message);
    }

    private void sendApplicationRequestAcknowledgementMail(final ApplicationAcknowledgementMail acknowledgementMail){
        var message = templateMessageFactory.createMessage(acknowledgementMail);
        var subject = String.format("We’ve received your application request for %s",
                acknowledgementMail.propertyName());
        mailService.sendHtmlMail(MailMessage.builder().subject(subject).replyTo(acknowledgementMail.propertyEmail())
                .to(acknowledgementMail.email()).build(),message);
    }

    private void sendApplicationRequestAcknowledgementText(final ApplicationAcknowledgementText acknowledgementText){
        var message = templateMessageFactory.createMessage(acknowledgementText);
        textService.sendText(TextMessage.builder().from(acknowledgementText.propertyPhone())
                .to(acknowledgementText.phoneNumber()).message(message).build());
    }

    private String getEmailTo(final String propertyEmail){

        return Optional.ofNullable(propertyEmail).orElse("inquiries@renaissancerentals.com");

    }

}
