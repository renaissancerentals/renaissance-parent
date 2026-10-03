package com.renaissancerentals.assets.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.renaissancerentals.assets.service.AssetService;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AssetControllerTest {

    private final AssetService assetService = mock(AssetService.class);
    private final MockMvc mvc =
            MockMvcBuilders.standaloneSetup(new AssetController(assetService)).build();

    @Test
    void downloadsCanBeKeptByTheBrowserForADay() throws Exception {
        when(assetService.getFile("abc")).thenReturn(Optional.of("photo-bytes".getBytes()));

        var response = mvc.perform(get("/api/assets/abc/download")).andReturn().getResponse();

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsByteArray()).isEqualTo("photo-bytes".getBytes());
        assertThat(response.getHeader(HttpHeaders.CACHE_CONTROL))
                .contains("max-age=86400")
                .contains("public");
        assertThat(response.getHeader(HttpHeaders.CONTENT_DISPOSITION)).contains("attachment");
    }

    @Test
    void noValidatorIsSentBecauseCheckingOneWouldMeanDownloadingTheFileFromDriveAgain() throws Exception {
        when(assetService.getFile("abc")).thenReturn(Optional.of("photo-bytes".getBytes()));

        var response = mvc.perform(get("/api/assets/abc/download")).andReturn().getResponse();

        assertThat(response.getHeader(HttpHeaders.ETAG)).isNull();
    }
}
