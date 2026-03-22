package com.renaissancerentals.foundation.ratelimiter;

import java.time.Duration;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ConditionalOnWebApplication
@AutoConfigureAfter(name = "org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration")
@EnableScheduling
public class RateLimiterAutoconfiguration {
    @Bean
    @ConditionalOnMissingBean
    public RateLimiter inMemoryErrorRateLimiter(){
        return new InMemoryErrorRateLimiter(Duration.ofMinutes(5), 1);
    }
}
