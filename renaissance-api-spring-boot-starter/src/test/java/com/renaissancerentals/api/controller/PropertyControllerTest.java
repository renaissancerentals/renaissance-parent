package com.renaissancerentals.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.renaissancerentals.api.controller.converter.ProjectionConverter;
import com.renaissancerentals.api.domain.Faq;
import com.renaissancerentals.api.domain.projection.FloorplanListing;
import com.renaissancerentals.api.domain.projection.PropertyDetails;
import com.renaissancerentals.api.domain.projection.PropertyListing;
import com.renaissancerentals.api.service.PropertyService;
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

@WebMvcTest(controllers = PropertyController.class)
@ContextConfiguration(classes = {PropertyController.class, ProjectionConverter.class, GlobalExceptionHandler.class})
class PropertyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PropertyService propertyService;

    @MockitoBean
    private ExceptionNotifier<ServerException> exceptionNotifier;

    @MockitoBean
    private ExecutorService virtualThreadExecutor;

    @Test
    void getAllWithFilterProjectionReturnsListings() throws Exception {
        when(propertyService.getPropertyListings())
                .thenReturn(List.of(PropertyListing.builder().id("p-1").build()));

        mockMvc.perform(get("/api/properties").param("projection", "filter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("p-1"));
    }

    @Test
    void getAllWithUnsupportedProjectionReturns400() throws Exception {
        mockMvc.perform(get("/api/properties").param("projection", "details"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getWithDetailsProjectionReturnsProperty() throws Exception {
        when(propertyService.getProperty("p-1")).thenReturn(PropertyDetails.builder().id("p-1").build());

        mockMvc.perform(get("/api/properties/p-1").param("projection", "details"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("p-1"));
    }

    @Test
    void getWithUnsupportedProjectionReturns400() throws Exception {
        mockMvc.perform(get("/api/properties/p-1").param("projection", "filter"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getFloorplansWithFilterProjectionReturnsListings() throws Exception {
        when(propertyService.getFloorplanListingsForProperty("p-1"))
                .thenReturn(List.of(FloorplanListing.builder().id("f-1").build()));

        mockMvc.perform(get("/api/properties/p-1/floorplans").param("projection", "filter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("f-1"));
    }

    @Test
    void getFloorplansWithUnsupportedProjectionReturns400() throws Exception {
        mockMvc.perform(get("/api/properties/p-1/floorplans").param("projection", "details"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getFaqsDelegatesToService() throws Exception {
        when(propertyService.getPropertyFaqs("p-1")).thenReturn(List.of(new Faq(1L, "Q", "A", 1.0f)));

        mockMvc.perform(get("/api/properties/p-1/faqs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].question").value("Q"));
    }
}
