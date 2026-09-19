package com.renaissancerentals.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.renaissancerentals.api.domain.JobVacancy;
import com.renaissancerentals.api.repository.JobVacancyRepository;
import com.renaissancerentals.foundation.error.GlobalExceptionHandler;
import com.renaissancerentals.foundation.error.ServerException;
import com.renaissancerentals.foundation.error.notification.component.ExceptionNotifier;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = JobVacancyController.class)
@ContextConfiguration(classes = {JobVacancyController.class, GlobalExceptionHandler.class})
class JobVacancyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JobVacancyRepository jobVacancyRepository;

    @MockitoBean
    private ExceptionNotifier<ServerException> exceptionNotifier;

    @MockitoBean
    private ExecutorService virtualThreadExecutor;

    @Test
    void getJobVacancyReturnsVacancyWhenFound() throws Exception {
        when(jobVacancyRepository.getJobVacancy(1L))
                .thenReturn(Optional.of(JobVacancy.builder().id(1L).title("Leasing Agent").build()));

        mockMvc.perform(get("/api/jobVacancies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Leasing Agent"));
    }

    @Test
    void getJobVacancyReturns404WhenMissing() throws Exception {
        when(jobVacancyRepository.getJobVacancy(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/jobVacancies/999")).andExpect(status().isNotFound());
    }

    @Test
    void getJobVacanciesReturnsActiveVacancies() throws Exception {
        when(jobVacancyRepository.getActiveJobVacancies())
                .thenReturn(List.of(JobVacancy.builder().id(1L).title("Leasing Agent").build()));

        mockMvc.perform(get("/api/jobVacancies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Leasing Agent"));
    }
}
