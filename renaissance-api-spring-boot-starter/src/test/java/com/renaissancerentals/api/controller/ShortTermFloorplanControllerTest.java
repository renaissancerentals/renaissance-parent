package com.renaissancerentals.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.renaissancerentals.api.controller.converter.ProjectionConverter;
import com.renaissancerentals.api.domain.ShortTermFloorplan;
import com.renaissancerentals.api.service.ShortTermService;
import com.renaissancerentals.foundation.error.GlobalExceptionHandler;
import com.renaissancerentals.foundation.error.ServerException;
import com.renaissancerentals.foundation.error.notification.component.ExceptionNotifier;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ShortTermFloorplanController.class)
@ContextConfiguration(
        classes = {ShortTermFloorplanController.class, ProjectionConverter.class, GlobalExceptionHandler.class})
class ShortTermFloorplanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShortTermService shortTermService;

    @MockitoBean
    private ExceptionNotifier<ServerException> exceptionNotifier;

    @MockitoBean
    private ExecutorService virtualThreadExecutor;

    @Test
    void getWithDetailsProjectionReturnsShortTermFloorplan() throws Exception {
        when(shortTermService.getShortTermFloorplan("f-1"))
                .thenReturn(ShortTermFloorplan.builder().id("f-1").build());

        mockMvc.perform(get("/api/shortTermFloorplans/f-1").param("projection", "details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("f-1"));
    }

    @Test
    void getWithUnsupportedProjectionReturns404() throws Exception {
        mockMvc.perform(get("/api/shortTermFloorplans/f-1").param("projection", "spotlight"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getShortTermsByPropertyWithDetailsProjectionReturnsList() throws Exception {
        when(shortTermService.getShortTermFloorplansForProperty("p-1"))
                .thenReturn(List.of(ShortTermFloorplan.builder().id("f-1").build()));

        mockMvc.perform(get("/api/shortTermFloorplans/byPropertyId/p-1").param("projection", "details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("f-1"));
    }

    @Test
    void getShortTermsByPropertyWithUnsupportedProjectionReturns400() throws Exception {
        mockMvc.perform(get("/api/shortTermFloorplans/byPropertyId/p-1").param("projection", "spotlight"))
                .andExpect(status().isBadRequest());
    }
}
