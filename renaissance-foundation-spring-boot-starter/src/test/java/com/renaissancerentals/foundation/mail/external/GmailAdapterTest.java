package com.renaissancerentals.foundation.mail.external;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.Message;
import com.renaissancerentals.foundation.mail.config.MailConfigProperties;
import com.renaissancerentals.foundation.mail.error.MailErrorCode;
import com.renaissancerentals.foundation.mail.error.MailServerException;
import com.renaissancerentals.foundation.mail.model.MailMessage;

@ExtendWith(MockitoExtension.class)
class GmailAdapterTest {

    @Mock
    MailConfigProperties config;

    @Mock
    GmailFactory gmailFactory;

    @Mock
    Gmail gmail;

    @Mock
    Gmail.Users users;

    @Mock
    Gmail.Users.Messages messages;

    @Mock
    Gmail.Users.Messages.Send send;

    GmailAdapter adapter;

    @BeforeEach
    void setUp(){
        when(gmailFactory.create()).thenReturn(gmail);
        when(config.from()).thenReturn("noreply@example.com");
        when(config.fromName()).thenReturn("Mail Bot");
        adapter = new GmailAdapter(config, gmailFactory);
    }

    @Test
    void shouldSendMailSuccessfully() throws Exception{
        MailMessage mail = MailMessage.builder().to("to@example.com").subject("Test Subject")
                .replyTo("reply@example.com").build();

        when(gmail.users()).thenReturn(users);
        when(users.messages()).thenReturn(messages);
        when(messages.send(eq("me"),any(Message.class))).thenReturn(send);
        when(send.execute()).thenReturn(new Message());

        assertDoesNotThrow(() -> adapter.sendMail(mail,"Hello from test"));
        verify(send).execute();
    }

    @Test
    void shouldThrowMailServerExceptionWhenGmailFails() throws Exception{
        MailMessage mail = MailMessage.builder().to("to@example.com").subject("Failure Test")
                .replyTo("reply@example.com").build();

        when(gmail.users()).thenReturn(users);
        when(users.messages()).thenReturn(messages);
        when(messages.send(eq("me"),any(Message.class))).thenReturn(send);
        when(send.execute()).thenThrow(new IOException("Service unavailable"));

        MailServerException ex = assertThrows(MailServerException.class,() -> {
            adapter.sendMail(mail,"Test body");
        });

        assertEquals(MailErrorCode.MAIL_SEND_ERROR,ex.getErrorCode());
    }
}
