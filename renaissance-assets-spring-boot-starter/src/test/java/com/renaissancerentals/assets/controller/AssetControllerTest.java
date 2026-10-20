package com.renaissancerentals.assets.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.asyncDispatch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import com.renaissancerentals.assets.error.AssetsNotFoundException;
import com.renaissancerentals.assets.model.Asset;
import com.renaissancerentals.assets.service.AssetService;
import java.io.OutputStream;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

class AssetControllerTest {

    private final AssetService assetService = mock(AssetService.class);
    private final MockMvc mvc =
            MockMvcBuilders.standaloneSetup(new AssetController(assetService)).build();

    private MockHttpServletResponse download(String id) throws Exception {
        var started = mvc.perform(get("/api/assets/" + id + "/download")).andReturn();
        return mvc.perform(asyncDispatch(started)).andReturn().getResponse();
    }

    private void driveHolds(String id, String content) throws Exception {
        when(assetService.get(id)).thenReturn(Optional.of(Asset.builder().id(id).build()));
        doAnswer(invocation -> {
                    invocation.<OutputStream>getArgument(1).write(content.getBytes());
                    return null;
                })
                .when(assetService)
                .downloadTo(org.mockito.ArgumentMatchers.eq(id), any());
    }

    @Test
    void downloadsCanBeKeptByTheBrowserForADay() throws Exception {
        driveHolds("abc", "photo-bytes");

        var response = download("abc");

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(response.getContentAsByteArray()).isEqualTo("photo-bytes".getBytes());
        assertThat(response.getHeader(HttpHeaders.CACHE_CONTROL))
                .contains("max-age=86400")
                .contains("public");
        assertThat(response.getHeader(HttpHeaders.CONTENT_DISPOSITION)).contains("attachment");
    }

    @Test
    void noValidatorIsSentBecauseCheckingOneWouldMeanDownloadingTheFileFromDriveAgain() throws Exception {
        driveHolds("abc", "photo-bytes");

        var response = download("abc");

        assertThat(response.getHeader(HttpHeaders.ETAG)).isNull();
    }

    @Test
    void aMissingFileIsAskedAboutBeforeAnythingIsStreamed() {
        when(assetService.get("gone")).thenReturn(Optional.empty());

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> mvc.perform(get("/api/assets/gone/download")))
                .hasRootCauseInstanceOf(AssetsNotFoundException.class);
        try {
            verify(assetService, never()).downloadTo(any(), any());
        } catch (java.io.IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
