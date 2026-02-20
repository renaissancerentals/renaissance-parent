package com.renaissancerentals.api.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TextConfigProperties.class)
@ConditionalOnProperty(name = "renaissancerentals.text.enabled", havingValue = "true", matchIfMissing = true)
public class TextAutoConfiguration {


}
