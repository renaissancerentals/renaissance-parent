package com.renaissancerentals.api.config;

import com.renaissancerentals.api.cache.ApiCacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({PropertyConfigProperties.class, ApiCacheProperties.class})
public class ApiAutoConfiguration {}
