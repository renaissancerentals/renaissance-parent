package com.renaissancerentals.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renaissancerentals.api.messaging.ContactMessageRequest;
import com.renaissancerentals.api.service.ContactService;
import com.renaissancerentals.foundation.error.GlobalExceptionHandler;
import com.renaissancerentals.foundation.error.ServerException;
import com.renaissancerentals.foundation.error.notification.component.ExceptionNotifier;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ContactController.class)
@ContextConfiguration(classes = {ContactController.class, GlobalExceptionHandler.class})
class ContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ContactService contactService;

    @MockitoBean
    private ExceptionNotifier<ServerException> exceptionNotifier;

    @MockitoBean
    private ExecutorService virtualThreadExecutor;

    private ContactMessageRequest validRequest(String preferredName) {
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
                preferredName);
    }

    @Test
    void postContactWithValidBodySavesAndReturns200() throws Exception {
        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest(null))))
                .andExpect(status().isOk());

        verify(contactService).save(any());
    }

    @Test
    void postContactWithHoneypotFilledIsRejectedAsBotAndNotSaved() throws Exception {
        mockMvc.perform(post("/api/contact")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest("filled-by-a-bot"))))
                .andExpect(status().isBadRequest());

        verify(contactService, never()).save(any());
    }

    @Test
    void postContactMissingRequiredFieldReturns400() throws Exception {
        String missingEmail =
                """
                {"firstName":"Jane","lastName":"Doe","phone":"8125550000","property":"p-1","currentPage":"https://site/contact"}
                """;

        mockMvc.perform(post("/api/contact").contentType(MediaType.APPLICATION_JSON).content(missingEmail))
                .andExpect(status().isBadRequest());

        verify(contactService, never()).save(any());
    }
}
