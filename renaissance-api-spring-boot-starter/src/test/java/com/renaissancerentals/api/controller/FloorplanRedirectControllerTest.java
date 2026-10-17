package com.renaissancerentals.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.renaissancerentals.api.repository.FloorplanRepository;
import com.renaissancerentals.foundation.error.GlobalExceptionHandler;
import com.renaissancerentals.foundation.error.ServerException;
import com.renaissancerentals.foundation.error.notification.component.ExceptionNotifier;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = FloorplanRedirectController.class)
@ContextConfiguration(classes = {FloorplanRedirectController.class, GlobalExceptionHandler.class})
class FloorplanRedirectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FloorplanRepository floorplanRepository;

    @MockitoBean
    private ExceptionNotifier<ServerException> exceptionNotifier;

    @MockitoBean
    private ExecutorService virtualThreadExecutor;

    @Test
    void anOldAddressAnswersWithTheCurrentId() throws Exception {
        when(floorplanRepository.findCurrentIdOfRenamed("view-big")).thenReturn(Optional.of("view"));

        mockMvc.perform(get("/api/floorplanRedirects/view-big"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from").value("view-big"))
                .andExpect(jsonPath("$.to").value("view"));
    }

    @Test
    void anAddressNobodyRenamedIsNotFound() throws Exception {
        when(floorplanRepository.findCurrentIdOfRenamed("nope")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/floorplanRedirects/nope")).andExpect(status().isNotFound());
    }
}
