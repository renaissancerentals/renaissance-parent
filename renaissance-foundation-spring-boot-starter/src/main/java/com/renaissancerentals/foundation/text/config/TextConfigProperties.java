package com.renaissancerentals.foundation.text.config;

import com.renaissancerentals.foundation.text.data.TextExtension;
import java.util.Map;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "renaissancerentals.text")
public record TextConfigProperties(boolean enabled, String serverUrl, Map<String, TextExtension> extensions) {
    public TextConfigProperties {
        extensions = extensions == null ? Map.of() : Map.copyOf(extensions);
    }
}
