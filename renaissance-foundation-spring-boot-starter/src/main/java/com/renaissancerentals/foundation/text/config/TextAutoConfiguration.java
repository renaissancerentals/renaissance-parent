package com.renaissancerentals.foundation.text.config;

import com.renaissancerentals.foundation.text.external.RingCentralTextService;
import com.renaissancerentals.foundation.text.service.TextService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(TextConfigProperties.class)
@ConditionalOnProperty(name = "renaissancerentals.text.enabled", havingValue = "true", matchIfMissing = true)
public class TextAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public TextService textService(TextConfigProperties config) {
        return new RingCentralTextService(config);
    }
}
