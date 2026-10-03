package com.renaissancerentals.api.cache;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.StaticListableBeanFactory;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

class ApiCacheAdviceTest {

    @RestController
    @RequestMapping("/api")
    static class Sample {
        @GetMapping("/properties/{id}")
        ResponseEntity<Map<String, String>> property(@PathVariable("id") String id) {
            if (id.equals("missing")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "not found"));
            }
            return ResponseEntity.ok(Map.of("id", id));
        }

        @GetMapping("/properties")
        Map<String, String> properties() {
            return Map.of("a", "b");
        }

        @GetMapping("/propertiesX")
        Map<String, String> lookalike() {
            return Map.of("a", "b");
        }

        @GetMapping("/sublets")
        Map<String, String> sublets() {
            return Map.of("email", "someone@example.com");
        }

        @GetMapping("/assets/{id}/download")
        ResponseEntity<String> asset(@PathVariable("id") String id) {
            return ResponseEntity.ok()
                    .cacheControl(CacheControl.maxAge(Duration.ofDays(1)).cachePublic())
                    .body("bytes");
        }

        @GetMapping("/floorplans/{id}")
        ResponseEntity<Void> noContent(@PathVariable("id") String id) {
            return ResponseEntity.noContent().build();
        }

        @PostMapping("/properties/{id}")
        Map<String, String> write(@PathVariable("id") String id) {
            return Map.of("saved", id);
        }
    }

    private static MockMvc mvc(ApiCacheProperties properties) {
        var beans = new StaticListableBeanFactory();
        if (properties != null) {
            beans.addBean("props", properties);
        }
        return MockMvcBuilders.standaloneSetup(new Sample())
                .setControllerAdvice(new ApiCacheAdvice(beans.getBeanProvider(ApiCacheProperties.class)))
                .build();
    }

    private static String cacheControl(MockMvc mvc, org.springframework.test.web.servlet.RequestBuilder request)
            throws Exception {
        return mvc.perform(request).andReturn().getResponse().getHeader(HttpHeaders.CACHE_CONTROL);
    }

    @Test
    void listingsAreCacheableForAMinuteByDefault() throws Exception {
        var header = cacheControl(mvc(null), get("/api/properties/high-grove"));

        assertThat(header).contains("max-age=60").contains("public").contains("stale-while-revalidate=120");
    }

    @Test
    void theListEndpointItselfIsCovered() throws Exception {
        assertThat(cacheControl(mvc(null), get("/api/properties"))).contains("max-age=60");
    }

    @Test
    void sublets_withContactDetails_areNeverCached() throws Exception {
        assertThat(cacheControl(mvc(null), get("/api/sublets"))).isNull();
    }

    @Test
    void errorsAreNotCached() throws Exception {
        assertThat(cacheControl(mvc(null), get("/api/properties/missing"))).isNull();
    }

    @Test
    void writesAreNotCached() throws Exception {
        assertThat(cacheControl(mvc(null), post("/api/properties/x"))).isNull();
    }

    @Test
    void anEndpointThatSetsItsOwnCacheControlIsLeftAlone() throws Exception {
        assertThat(cacheControl(mvc(null), get("/api/assets/abc/download"))).contains("max-age=86400");
    }

    @Test
    void emptyResponsesAreLeftAlone() throws Exception {
        assertThat(cacheControl(mvc(null), get("/api/floorplans/x"))).isNull();
    }

    @Test
    void theTimesAndPathsAreConfigurable() throws Exception {
        var properties =
                new ApiCacheProperties(true, Duration.ofMinutes(5), Duration.ofSeconds(30), List.of("/api/sublets"));
        var mvc = mvc(properties);

        assertThat(cacheControl(mvc, get("/api/sublets")))
                .contains("max-age=300")
                .contains("stale-while-revalidate=30");
        assertThat(cacheControl(mvc, get("/api/properties/x"))).isNull();
    }

    @Test
    void canBeSwitchedOff() throws Exception {
        var properties = new ApiCacheProperties(false, null, null, null);

        assertThat(cacheControl(mvc(properties), get("/api/properties/x"))).isNull();
    }

    @Test
    void aSimilarlyNamedPathIsNotMatched() throws Exception {
        // "/api/propertiesX" must not match the "/api/properties" prefix
        assertThat(cacheControl(mvc(null), get("/api/propertiesX"))).isNull();
    }
}
