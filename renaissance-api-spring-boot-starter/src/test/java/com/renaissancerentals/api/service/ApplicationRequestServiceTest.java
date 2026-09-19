package com.renaissancerentals.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.renaissancerentals.api.domain.TeamMember;
import com.renaissancerentals.api.domain.mapper.ApplicationRequestMapper;
import com.renaissancerentals.api.domain.projection.PropertyContact;
import com.renaissancerentals.api.messaging.ApplicationRequest;
import com.renaissancerentals.foundation.mail.model.MailMessage;
import com.renaissancerentals.foundation.mail.service.MailService;
import com.renaissancerentals.foundation.template.TemplateMessageFactory;
import com.renaissancerentals.foundation.text.service.TextService;
import com.renaissancerentals.persistence.dao.ApplicationEmailDao;
import com.renaissancerentals.persistence.entity.ApplicationEmailEntity;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ApplicationRequestServiceTest {

    @Mock
    private ApplicationEmailDao applicationEmailDao;

    @Mock
    private ApplicationRequestMapper applicationRequestMapper;

    @Mock
    private PropertyService propertyService;

    @Mock
    private MailService mailService;

    @Mock
    private TextService textService;

    @Mock
    private TemplateMessageFactory templateMessageFactory;

    @Mock
    private ExecutorService virtualThreadExecutor;

    private ApplicationRequestService applicationRequestService() {
        return new ApplicationRequestService(
                applicationEmailDao,
                applicationRequestMapper,
                propertyService,
                mailService,
                textService,
                templateMessageFactory,
                virtualThreadExecutor,
                List.of("base-cc@example.com"));
    }

    @BeforeEach
    void runExecutorSynchronously() {
        doAnswer(invocation -> {
                    ((Runnable) invocation.getArgument(0)).run();
                    return null;
                })
                .when(virtualThreadExecutor)
                .execute(any(Runnable.class));
    }

    private ApplicationRequest requestWithPhone(String phone) {
        return new ApplicationRequest(
                "Jane", "Doe", "jane@example.com", phone, "p-1", "https://site/apply", "Summer House", null, null,
                null);
    }

    @Test
    void saveMarksEntityNewAndPersistsIt() {
        ApplicationEmailEntity entity = new ApplicationEmailEntity();
        when(applicationRequestMapper.toEntity(any())).thenReturn(entity);
        when(propertyService.getPropertyContact("p-1"))
                .thenReturn(new PropertyContact("Summer House", "sh@example.com", null, "8125559999"));
        when(propertyService.getPropertyManager("p-1"))
                .thenReturn(TeamMember.builder().name("Alice").build());
        when(templateMessageFactory.createMessage(any())).thenReturn("rendered");

        applicationRequestService().save(requestWithPhone("8125550000"));

        assertThat(entity.isNew()).isTrue();
        assertThat(entity.getId()).isNotNull();
        assertThat(entity.getCreatedAt()).isNotNull();
        verify(applicationEmailDao).save(entity);
    }

    @Test
    void saveSendsApplicationEmailWithSecondaryEmailAddedToCc() {
        when(applicationRequestMapper.toEntity(any())).thenReturn(new ApplicationEmailEntity());
        when(propertyService.getPropertyContact("p-1"))
                .thenReturn(new PropertyContact("Summer House", "sh@example.com", "secondary@example.com", "8125559999"));
        when(propertyService.getPropertyManager("p-1"))
                .thenReturn(TeamMember.builder().name("Alice").build());
        when(templateMessageFactory.createMessage(any())).thenReturn("rendered");

        applicationRequestService().save(requestWithPhone("8125550000"));

        ArgumentCaptor<MailMessage> captor = ArgumentCaptor.forClass(MailMessage.class);
        verify(mailService).sendMail(captor.capture(), anyString());
        assertThat(captor.getValue().to()).isEqualTo("sh@example.com");
        assertThat(captor.getValue().cc()).containsExactlyInAnyOrder("base-cc@example.com", "secondary@example.com");
    }

    @Test
    void saveFallsBackToDefaultEmailWhenPropertyHasNone() {
        when(applicationRequestMapper.toEntity(any())).thenReturn(new ApplicationEmailEntity());
        when(propertyService.getPropertyContact("p-1")).thenReturn(new PropertyContact("Summer House", null, null, null));
        when(propertyService.getPropertyManager("p-1"))
                .thenReturn(TeamMember.builder().name("Alice").build());
        when(templateMessageFactory.createMessage(any())).thenReturn("rendered");

        applicationRequestService().save(requestWithPhone("8125550000"));

        ArgumentCaptor<MailMessage> captor = ArgumentCaptor.forClass(MailMessage.class);
        verify(mailService).sendMail(captor.capture(), anyString());
        assertThat(captor.getValue().to()).isEqualTo("inquiries@renaissancerentals.com");
    }

    @Test
    void saveSendsTextAcknowledgementWhenPhoneProvided() {
        when(applicationRequestMapper.toEntity(any())).thenReturn(new ApplicationEmailEntity());
        when(propertyService.getPropertyContact("p-1"))
                .thenReturn(new PropertyContact("Summer House", "sh@example.com", null, "8125559999"));
        when(propertyService.getPropertyManager("p-1"))
                .thenReturn(TeamMember.builder().name("Alice").build());
        when(templateMessageFactory.createMessage(any())).thenReturn("rendered");

        applicationRequestService().save(requestWithPhone("8125550000"));

        verify(mailService).sendHtmlMail(any(), anyString());
        verify(textService).sendText(any());
    }

    @Test
    void saveSkipsTextAcknowledgementWhenPhoneMissing() {
        when(applicationRequestMapper.toEntity(any())).thenReturn(new ApplicationEmailEntity());
        when(propertyService.getPropertyContact("p-1"))
                .thenReturn(new PropertyContact("Summer House", "sh@example.com", null, "8125559999"));
        when(propertyService.getPropertyManager("p-1"))
                .thenReturn(TeamMember.builder().name("Alice").build());
        when(templateMessageFactory.createMessage(any())).thenReturn("rendered");

        applicationRequestService().save(requestWithPhone(null));

        verify(mailService).sendHtmlMail(any(), anyString());
        verify(textService, never()).sendText(any());
    }
}
