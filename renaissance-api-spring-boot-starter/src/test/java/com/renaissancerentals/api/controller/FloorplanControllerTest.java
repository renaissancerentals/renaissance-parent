package com.renaissancerentals.api.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.renaissancerentals.api.controller.converter.ProjectionConverter;
import com.renaissancerentals.api.domain.Faq;
import com.renaissancerentals.api.domain.Floorplan;
import com.renaissancerentals.api.domain.SimilarFloorplan;
import com.renaissancerentals.api.domain.projection.FloorplanDetails;
import com.renaissancerentals.api.domain.projection.FloorplanSpotlight;
import com.renaissancerentals.api.repository.FloorplanRepository;
import com.renaissancerentals.api.service.FloorplanService;
import com.renaissancerentals.foundation.error.GlobalExceptionHandler;
import com.renaissancerentals.foundation.error.ServerException;
import com.renaissancerentals.foundation.error.notification.component.ExceptionNotifier;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

@WebMvcTest(controllers = FloorplanController.class)
@ContextConfiguration(
        classes = {FloorplanController.class, ProjectionConverter.class, GlobalExceptionHandler.class})
class FloorplanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FloorplanRepository floorplanRepository;

    @MockitoBean
    private FloorplanService floorplanService;

    @MockitoBean
    private ExceptionNotifier<ServerException> exceptionNotifier;

    @MockitoBean
    private ExecutorService virtualThreadExecutor;

    @Test
    void getWithEnrichedProjectionDelegatesToService() throws Exception {
        when(floorplanService.getFloorplan("f-1"))
                .thenReturn(Floorplan.builder().id("f-1").name("Floorplan One").build());

        mockMvc.perform(get("/api/floorplans/f-1").param("projection", "enriched"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("f-1"))
                .andExpect(jsonPath("$.name").value("Floorplan One"));
    }

    @Test
    void getWithDetailsProjectionReturnsFloorplanDetails() throws Exception {
        when(floorplanRepository.getFloorplanDetails("f-1"))
                .thenReturn(Optional.of(FloorplanDetails.builder().id("f-1").build()));

        mockMvc.perform(get("/api/floorplans/f-1").param("projection", "details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("f-1"));
    }

    @Test
    void getWithDetailsProjectionReturns404WhenMissing() throws Exception {
        when(floorplanRepository.getFloorplanDetails("missing")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/floorplans/missing").param("projection", "details"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getWithSpotlightProjectionReturnsFloorplanSpotlight() throws Exception {
        when(floorplanRepository.getFloorplanSpotlight("f-1"))
                .thenReturn(Optional.of(FloorplanSpotlight.builder().id("f-1").build()));

        mockMvc.perform(get("/api/floorplans/f-1").param("projection", "spotlight"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("f-1"));
    }

    @Test
    void getWithUnsupportedProjectionValueReturns400() throws Exception {
        mockMvc.perform(get("/api/floorplans/f-1").param("projection", "not-a-real-projection"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getFloorplansByPropertyIdWithDetailsProjectionReturnsList() throws Exception {
        when(floorplanRepository.getFloorplanDetailsForProperty("p-1"))
                .thenReturn(List.of(FloorplanDetails.builder().id("f-1").build()));

        mockMvc.perform(get("/api/floorplans/byPropertyId/p-1").param("projection", "details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("f-1"));
    }

    @Test
    void getFloorplansByPropertyIdWithUnsupportedProjectionReturns400() throws Exception {
        mockMvc.perform(get("/api/floorplans/byPropertyId/p-1").param("projection", "enriched"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getFloorplansWithSpotlightAndFeaturedFilterReturnsFeaturedSpotlights() throws Exception {
        when(floorplanRepository.getFeaturedSpotlights())
                .thenReturn(List.of(FloorplanSpotlight.builder().id("f-1").build()));

        mockMvc.perform(get("/api/floorplans").param("projection", "spotlight").param("filterBy", "featured"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("f-1"));
    }

    @Test
    void getFloorplansWithSpotlightAndUnsupportedFilterReturns400() throws Exception {
        mockMvc.perform(
                        get("/api/floorplans").param("projection", "spotlight").param("filterBy", "unsupported"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getFloorplansWithDetailsProjectionReturnsActiveFloorplans() throws Exception {
        when(floorplanRepository.getActiveFloorplansDetails())
                .thenReturn(List.of(FloorplanDetails.builder().id("f-1").build()));

        mockMvc.perform(get("/api/floorplans").param("projection", "details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("f-1"));
    }

    @Test
    void getSimilarFloorplansDelegatesToService() throws Exception {
        when(floorplanService.findSimilarFloorplans("f-1")).thenReturn(List.of(new SimilarFloorplan("f-2")));

        mockMvc.perform(get("/api/floorplans/f-1/similar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].similarFloorplanId").value("f-2"));
    }

    @Test
    void getFloorplanFaqsDelegatesToService() throws Exception {
        when(floorplanService.findFloorplanFaqs("f-1")).thenReturn(List.of(new Faq(1L, "Q", "A", 1.0f)));

        mockMvc.perform(get("/api/floorplans/f-1/faqs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].question").value("Q"));
    }
}
