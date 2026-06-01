package com.renaissancerentals.foundation.management;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration(proxyBeanMethods = false)
@PropertySource("classpath:renaissance-foundation-defaults.properties")
public class ManagementAutoConfiguration {}
