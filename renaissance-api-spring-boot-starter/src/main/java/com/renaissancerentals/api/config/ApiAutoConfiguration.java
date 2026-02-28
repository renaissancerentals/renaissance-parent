package com.renaissancerentals.api.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PropertyConfigProperties.class)
public class ApiAutoConfiguration {

}
