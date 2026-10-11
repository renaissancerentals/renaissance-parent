package com.renaissancerentals.api.config;

import com.renaissancerentals.api.blog.BlogProperties;
import com.renaissancerentals.api.cache.ApiCacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({PropertyConfigProperties.class, ApiCacheProperties.class, BlogProperties.class})
public class ApiAutoConfiguration {}
