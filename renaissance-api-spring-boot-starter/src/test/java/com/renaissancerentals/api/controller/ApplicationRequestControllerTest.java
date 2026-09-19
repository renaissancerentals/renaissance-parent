package com.renaissancerentals.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renaissancerentals.api.messaging.ApplicationRequest;
import com.renaissancerentals.api.service.ApplicationRequestService;
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

@WebMvcTest(controllers = ApplicationRequestController.class)
@ContextConfiguration(classes = {ApplicationRequestController.class, GlobalExceptionHandler.class})
class ApplicationRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ApplicationRequestService applicationRequestService;

    @MockitoBean
    private ExceptionNotifier<ServerException> exceptionNotifier;

    @MockitoBean
    private ExecutorService virtualThreadExecutor;

    private ApplicationRequest validRequest(String preferredName) {
        return new ApplicationRequest(
                "Jane",
                "Doe",
                "jane@example.com",
                "8125550000",
                "p-1",
                "https://site/apply",
                "Summer House",
                null,
                null,
                preferredName);
    }

    @Test
    void postApplicationRequestWithValidBodySavesAndReturns200() throws Exception {
        mockMvc.perform(post("/api/applicationRequest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest(null))))
                .andExpect(status().isOk());

        verify(applicationRequestService).save(any());
    }

    @Test
    void postApplicationRequestWithHoneypotFilledIsRejectedAsBotAndNotSaved() throws Exception {
        mockMvc.perform(post("/api/applicationRequest")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest("filled-by-a-bot"))))
                .andExpect(status().isBadRequest());

        verify(applicationRequestService, never()).save(any());
    }

    @Test
    void postApplicationRequestMissingRequiredFieldReturns400() throws Exception {
        String missingEmail =
                """
                {"firstName":"Jane","lastName":"Doe","property":"p-1","currentPage":"https://site/apply","community":"Summer House"}
                """;

        mockMvc.perform(
                        post("/api/applicationRequest").contentType(MediaType.APPLICATION_JSON).content(missingEmail))
                .andExpect(status().isBadRequest());

        verify(applicationRequestService, never()).save(any());
    }
}
