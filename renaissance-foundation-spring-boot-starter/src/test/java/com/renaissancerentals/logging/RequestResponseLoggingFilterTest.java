package com.renaissancerentals.logging;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import nl.altindag.log.LogCaptor;

@WebMvcTest(controllers = DummyController.class)
@Import(RequestResponseLoggingFilter.class)
@AutoConfigureJsonTesters
class RequestResponseLoggingFilterTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldLogMaskedSensitiveData() throws Exception{
        List<String> logs;
        try (LogCaptor logCaptor = LogCaptor.forName("com.renaissancerentals.logging.RequestResponseLoggingFilter")) {
            logCaptor.setLogLevelToInfo();

            var json = """
                    {
                      "username": "user1",
                      "password": "my-secret-password",
                      "token": "abc123"
                    }
                    """;

            mockMvc.perform(post("/api/dummy/logs").contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization","Bearer real-token").header("Cookie","session-id=xyz").content(json))
                    .andExpect(status().isOk());

            logs = logCaptor.getInfoLogs();
        }
        assertThat(logs).anyMatch(log -> log.contains("Incoming Request") && log.contains("\"password\":\"***\"")
                && log.contains("\"token\":\"***\"") && log.contains("\"username\":\"user1\"")
                && log.contains("Authorization=***") && log.contains("Cookie=***"));

        assertThat(logs).noneMatch(log -> log.contains("my-secret-password") || log.contains("Bearer real-token"));
    }
}
