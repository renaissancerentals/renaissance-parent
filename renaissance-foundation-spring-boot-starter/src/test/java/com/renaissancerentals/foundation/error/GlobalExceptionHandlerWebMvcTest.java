package com.renaissancerentals.foundation.error;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.concurrent.ExecutorService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.renaissancerentals.foundation.error.notification.component.ExceptionNotifier;

@WebMvcTest(controllers = DummyErrorController.class)
@ContextConfiguration(classes = {DummyErrorController.class, GlobalExceptionHandler.class})
public class GlobalExceptionHandlerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ExceptionNotifier<ServerException> exceptionNotifier;

    @MockitoBean
    private ExecutorService virtualThreadExecutor;

    @Test
    void shouldHandleServerException() throws Exception{
        mockMvc.perform(get("/dummy/error/server")).andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.errorCode").value("SERVER_ERR"))
                .andExpect(jsonPath("$.errorMessage").value("Internal server error"));
    }

    @Test
    void shouldHandleClientException() throws Exception{
        mockMvc.perform(get("/dummy/error/client")).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("CLIENT_ERR"))
                .andExpect(jsonPath("$.errorMessage").value("Invalid request"));
    }

    @Test
    void shouldHandleBusinessException() throws Exception{
        mockMvc.perform(get("/dummy/error/business")).andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.errorCode").value("BUSINESS_ERR"))
                .andExpect(jsonPath("$.errorMessage").value("Violation"));
    }

    @Test
    void shouldHandleUnhandledException() throws Exception{
        mockMvc.perform(get("/dummy/error/unhandled")).andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.errorCode").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.errorMessage").value("Internal Server Error"));
    }
}
