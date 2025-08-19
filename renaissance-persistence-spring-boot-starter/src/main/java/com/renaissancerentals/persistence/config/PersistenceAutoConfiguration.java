package com.renaissancerentals.persistence.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;

@Configuration
@EnableJdbcRepositories(basePackages = "com.renaissancerentals.persistence.dao")
public class PersistenceAutoConfiguration {
}
