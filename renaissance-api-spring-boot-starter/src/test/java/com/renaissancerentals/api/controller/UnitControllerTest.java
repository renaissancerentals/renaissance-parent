package com.renaissancerentals.api.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.renaissancerentals.api.controller.converter.ProjectionConverter;
import com.renaissancerentals.api.domain.projection.UnitAddress;
import com.renaissancerentals.api.domain.projection.UnitFloorplan;
import com.renaissancerentals.api.domain.projection.UnitUtilities;
import com.renaissancerentals.api.repository.UnitRepository;
import com.renaissancerentals.api.service.FloorplanService;
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

@WebMvcTest(controllers = UnitController.class)
@ContextConfiguration(classes = {UnitController.class, ProjectionConverter.class, GlobalExceptionHandler.class})
class UnitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UnitRepository unitRepository;

    @MockitoBean
    private FloorplanService floorplanService;

    @MockitoBean
    private ExceptionNotifier<ServerException> exceptionNotifier;

    @MockitoBean
    private ExecutorService virtualThreadExecutor;

    @Test
    void getAllWithAddressProjectionReturnsAddresses() throws Exception {
        when(unitRepository.getAllAddresses()).thenReturn(List.of(new UnitAddress("u-1", "1 Main St", "47401")));

        mockMvc.perform(get("/api/units").param("projection", "address"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("u-1"));
    }

    @Test
    void getAllWithUnsupportedProjectionReturns400() throws Exception {
        mockMvc.perform(get("/api/units").param("projection", "details"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getWithUtilitiesProjectionReturnsUnitUtilities() throws Exception {
        when(unitRepository.getUnitUtilities("u-1"))
                .thenReturn(UnitUtilities.builder().id("u-1").propertyEmail("p@example.com").build());

        mockMvc.perform(get("/api/units/u-1").param("projection", "utilities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.propertyEmail").value("p@example.com"));
    }

    @Test
    void getWithUnitFloorplanProjectionDelegatesToFloorplanService() throws Exception {
        UnitFloorplan unitFloorplan = UnitFloorplan.builder().id("u-1").build();
        when(floorplanService.getUnitFloorplan("u-1")).thenReturn(unitFloorplan);

        mockMvc.perform(get("/api/units/u-1").param("projection", "unit-floorplan")).andExpect(status().isOk());
    }

    @Test
    void getWithUnsupportedProjectionReturns400() throws Exception {
        mockMvc.perform(get("/api/units/u-1").param("projection", "address"))
                .andExpect(status().isBadRequest());
    }
}
