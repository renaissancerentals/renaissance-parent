package com.renaissancerentals.foundation.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

class CompressionDefaultsPostProcessorTest {

    private final CompressionDefaultsPostProcessor processor = new CompressionDefaultsPostProcessor();

    @Test
    void compressionIsOnByDefault() {
        var environment = new StandardEnvironment();

        processor.postProcessEnvironment(environment, null);

        assertThat(environment.getProperty("server.compression.enabled")).isEqualTo("true");
        assertThat(environment.getProperty("server.compression.min-response-size"))
                .isEqualTo("1KB");
    }

    @Test
    void aSiteCanSwitchItOff() {
        var environment = new StandardEnvironment();
        environment
                .getPropertySources()
                .addFirst(new MapPropertySource("site", Map.of("server.compression.enabled", "false")));

        processor.postProcessEnvironment(environment, null);

        assertThat(environment.getProperty("server.compression.enabled")).isEqualTo("false");
    }

    @Test
    void runningTwiceDoesNotDuplicateTheSource() {
        var environment = new StandardEnvironment();

        processor.postProcessEnvironment(environment, null);
        processor.postProcessEnvironment(environment, null);

        assertThat(environment.getPropertySources().stream()
                        .filter(s -> s.getName().equals(CompressionDefaultsPostProcessor.SOURCE_NAME)))
                .hasSize(1);
    }
}
