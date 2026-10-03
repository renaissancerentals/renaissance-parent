package com.renaissancerentals.api.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.renaissancerentals.api.controller.PropertyController;
import com.renaissancerentals.api.controller.converter.ProjectionConverter;
import com.renaissancerentals.api.domain.projection.PropertyListing;
import com.renaissancerentals.api.error.NotFoundException;
import com.renaissancerentals.api.service.PropertyService;
import com.renaissancerentals.foundation.error.GlobalExceptionHandler;
import com.renaissancerentals.foundation.error.ServerException;
import com.renaissancerentals.foundation.error.notification.component.ExceptionNotifier;
import java.util.List;
import java.util.concurrent.ExecutorService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/** The advice together with a real controller, the real error handler and property binding. */
@WebMvcTest(controllers = PropertyController.class)
@ContextConfiguration(
        classes = {
            PropertyController.class,
            ProjectionConverter.class,
            GlobalExceptionHandler.class,
            ApiCacheAdvice.class,
            ApiCacheWithRealControllersTest.Config.class
        })
@TestPropertySource(properties = "renaissancerentals.api.cache.max-age=5m")
class ApiCacheWithRealControllersTest {

    @EnableConfigurationProperties(ApiCacheProperties.class)
    static class Config {}

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PropertyService propertyService;

    @MockitoBean
    private ExceptionNotifier<ServerException> exceptionNotifier;

    @MockitoBean
    private ExecutorService virtualThreadExecutor;

    @Test
    void propertyListingsGetTheConfiguredCacheControl() throws Exception {
        when(propertyService.getPropertyListings())
                .thenReturn(List.of(PropertyListing.builder().id("p-1").build()));

        var response = mockMvc.perform(get("/api/properties").param("projection", "filter"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getHeader(HttpHeaders.CACHE_CONTROL))
                .contains("max-age=300")
                .contains("public");
    }

    @Test
    void aMissingPropertyIsNotCached() throws Exception {
        when(propertyService.getProperty("gone")).thenThrow(new NotFoundException("gone"));

        var response = mockMvc.perform(get("/api/properties/gone").param("projection", "details"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(404);
        assertThat(response.getHeader(HttpHeaders.CACHE_CONTROL)).isNull();
    }

    @Test
    void aBadRequestIsNotCached() throws Exception {
        var response = mockMvc.perform(get("/api/properties").param("projection", "details"))
                .andReturn()
                .getResponse();

        assertThat(response.getStatus()).isEqualTo(400);
        assertThat(response.getHeader(HttpHeaders.CACHE_CONTROL)).isNull();
    }
}
