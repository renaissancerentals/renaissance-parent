package com.renaissancerentals.foundation.web;

import java.util.Map;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

/**
 * Turns on HTTP response compression for every site. The single page apps ship a JavaScript bundle of well over a
 * megabyte that is otherwise sent uncompressed. Added with the lowest precedence, so a site can still set
 * {@code server.compression.*} itself (for example to switch it off behind a CDN that compresses).
 */
public class CompressionDefaultsPostProcessor implements EnvironmentPostProcessor, Ordered {

    static final String SOURCE_NAME = "renaissanceCompressionDefaults";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        if (environment.getPropertySources().contains(SOURCE_NAME)) {
            return;
        }
        environment
                .getPropertySources()
                .addLast(new MapPropertySource(
                        SOURCE_NAME,
                        Map.of(
                                "server.compression.enabled", "true",
                                "server.compression.min-response-size", "1KB")));
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
