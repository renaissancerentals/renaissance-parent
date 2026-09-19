package com.renaissancerentals.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renaissancerentals.api.domain.Sublet;
import com.renaissancerentals.api.messaging.SubletMessageRequest;
import com.renaissancerentals.api.messaging.SubletRequest;
import com.renaissancerentals.api.repository.SubletRepository;
import com.renaissancerentals.api.service.SubletMessageService;
import com.renaissancerentals.foundation.error.GlobalExceptionHandler;
import com.renaissancerentals.foundation.error.ServerException;
import com.renaissancerentals.foundation.error.notification.component.ExceptionNotifier;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = SubletController.class)
@ContextConfiguration(classes = {SubletController.class, GlobalExceptionHandler.class})
class SubletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private SubletMessageService subletMessageService;

    @MockitoBean
    private SubletRepository subletRepository;

    @MockitoBean
    private ExceptionNotifier<ServerException> exceptionNotifier;

    @MockitoBean
    private ExecutorService virtualThreadExecutor;

    private SubletRequest validRequest(String preferredName) {
        return new SubletRequest(
                "Jane",
                "Doe",
                "jane@example.com",
                2,
                2,
                LocalDate.of(2026, 1, 1),
                LocalDate.of(2026, 6, 1),
                1200f,
                true,
                true,
                "123 Main St",
                "47401",
                "Great sublet",
                "Description",
                preferredName);
    }

    @Test
    void createWithValidBodyPersistsAndSendsAlert() throws Exception {
        Sublet sublet = Sublet.builder().assetKey("s-1").firstName("Jane").build();
        when(subletRepository.createSublet(any())).thenReturn(sublet);

        mockMvc.perform(post("/api/sublets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest(null))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assetKey").value("s-1"));

        verify(subletMessageService).sendNewSubletAlert(sublet);
    }

    @Test
    void createWithHoneypotFilledIsRejectedAndNotPersisted() throws Exception {
        mockMvc.perform(post("/api/sublets")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest("filled-by-a-bot"))))
                .andExpect(status().isBadRequest());

        verify(subletRepository, never()).createSublet(any());
    }

    @Test
    void getAllReturnsAllSublets() throws Exception {
        when(subletRepository.getAll())
                .thenReturn(List.of(Sublet.builder().assetKey("s-1").build()));

        mockMvc.perform(get("/api/sublets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].assetKey").value("s-1"));
    }

    @Test
    void getReturnsSubletWhenFound() throws Exception {
        when(subletRepository.getSublet("s-1"))
                .thenReturn(Optional.of(Sublet.builder().assetKey("s-1").build()));

        mockMvc.perform(get("/api/sublets/s-1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assetKey").value("s-1"));
    }

    @Test
    void getReturns404WhenMissing() throws Exception {
        when(subletRepository.getSublet("missing")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/sublets/missing")).andExpect(status().isNotFound());
    }

    @Test
    void deleteDeactivatesSubletAndReturns204() throws Exception {
        mockMvc.perform(delete("/api/sublets/s-1")).andExpect(status().isNoContent());

        verify(subletRepository).deactivateSubletBy("s-1");
    }

    @Test
    void sendMessageDelegatesToServiceWhenSubletFound() throws Exception {
        Sublet sublet = Sublet.builder().assetKey("s-1").build();
        when(subletRepository.getSublet("s-1")).thenReturn(Optional.of(sublet));
        SubletMessageRequest message = new SubletMessageRequest("Hi there", "Jane", "jane@example.com", null);

        mockMvc.perform(post("/api/sublets/s-1/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(message)))
                .andExpect(status().isNoContent());

        verify(subletMessageService).sendMessage(sublet, message);
    }

    @Test
    void sendMessageReturns404WhenSubletMissing() throws Exception {
        when(subletRepository.getSublet("missing")).thenReturn(Optional.empty());
        SubletMessageRequest message = new SubletMessageRequest("Hi there", "Jane", "jane@example.com", null);

        mockMvc.perform(post("/api/sublets/missing/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(message)))
                .andExpect(status().isNotFound());
    }
}
