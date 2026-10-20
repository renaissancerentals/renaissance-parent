package com.renaissancerentals.foundation.web;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class BinaryResponseLoggingFilterTest {

    private final BinaryResponseLoggingFilter filter = new BinaryResponseLoggingFilter();

    private boolean skips(String uri) {
        var request = new MockHttpServletRequest("GET", uri);
        request.setRequestURI(uri);
        return filter.shouldNotFilter(request);
    }

    @Test
    void fileDownloadsAreNotBuffered() {
        assertThat(skips("/api/assets/abc/download")).isTrue();
        assertThat(skips("/api/blog/media/notes/123")).isTrue();
    }

    @Test
    void otherApiCallsAreStillLogged() {
        assertThat(skips("/api/assets/abc")).isFalse();
        assertThat(skips("/api/floorplans")).isFalse();
    }

    @Test
    void thePagesAreStillNotLogged() {
        assertThat(skips("/floorplans")).isTrue();
    }
}
