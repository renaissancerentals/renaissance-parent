package com.renaissancerentals.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.renaissancerentals.api.domain.TeamMember;
import com.renaissancerentals.api.domain.projection.PropertyContact;
import com.renaissancerentals.api.messaging.ContactMessageRequest;
import com.renaissancerentals.api.repository.ContactRepository;
import com.renaissancerentals.foundation.mail.model.MailMessage;
import com.renaissancerentals.foundation.mail.service.MailService;
import com.renaissancerentals.foundation.template.TemplateMessageFactory;
import com.renaissancerentals.foundation.text.service.TextService;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

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

    private ContactService contactService() {
        return new ContactService(
                contactRepository,
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

    private ContactMessageRequest request() {
        return new ContactMessageRequest(
                "Jane",
                "Doe",
                "jane@example.com",
                "8125550000",
                "Any pet-friendly units?",
                "p-1",
                "https://site/contact",
                false,
                false,
                true,
                null,
                null);
    }

    private void stubCommonCollaborators() {
        when(propertyService.getPropertyContact("p-1"))
                .thenReturn(new PropertyContact("Summer House", "sh@example.com", null, "8125559999"));
        when(propertyService.getPropertyManager("p-1"))
                .thenReturn(TeamMember.builder().name("Alice").build());
        when(propertyService.getPropertyUrl("p-1")).thenReturn("https://www.summerhouseatindiana.com/");
        when(templateMessageFactory.createMessage(any())).thenReturn("rendered");
    }

    @Test
    void savePersistsTheContactMessage() {
        stubCommonCollaborators();

        contactService().save(request());

        verify(contactRepository).save(request());
    }

    @Test
    void saveSendsContactEmailWithSecondaryEmailAddedToCc() {
        when(propertyService.getPropertyContact("p-1"))
                .thenReturn(
                        new PropertyContact("Summer House", "sh@example.com", "secondary@example.com", "8125559999"));
        when(propertyService.getPropertyManager("p-1"))
                .thenReturn(TeamMember.builder().name("Alice").build());
        when(propertyService.getPropertyUrl("p-1")).thenReturn("https://www.summerhouseatindiana.com/");
        when(templateMessageFactory.createMessage(any())).thenReturn("rendered");

        contactService().save(request());

        ArgumentCaptor<MailMessage> captor = ArgumentCaptor.forClass(MailMessage.class);
        verify(mailService).sendMail(captor.capture(), anyString());
        assertThat(captor.getValue().to()).isEqualTo("sh@example.com");
        assertThat(captor.getValue().cc()).containsExactlyInAnyOrder("base-cc@example.com", "secondary@example.com");
    }

    @Test
    void saveFallsBackToDefaultEmailWhenPropertyHasNone() {
        when(propertyService.getPropertyContact("p-1"))
                .thenReturn(new PropertyContact("Summer House", null, null, null));
        when(propertyService.getPropertyManager("p-1"))
                .thenReturn(TeamMember.builder().name("Alice").build());
        when(propertyService.getPropertyUrl("p-1")).thenReturn("https://www.summerhouseatindiana.com/");
        when(templateMessageFactory.createMessage(any())).thenReturn("rendered");

        contactService().save(request());

        ArgumentCaptor<MailMessage> captor = ArgumentCaptor.forClass(MailMessage.class);
        verify(mailService).sendMail(captor.capture(), anyString());
        assertThat(captor.getValue().to()).isEqualTo("inquiries@renaissancerentals.com");
    }

    @Test
    void saveSendsAcknowledgementMailAndTextWithPropertyUrl() {
        stubCommonCollaborators();

        contactService().save(request());

        verify(mailService).sendHtmlMail(any(), anyString());
        verify(textService).sendText(any());
        verify(propertyService).getPropertyUrl("p-1");
    }
}
