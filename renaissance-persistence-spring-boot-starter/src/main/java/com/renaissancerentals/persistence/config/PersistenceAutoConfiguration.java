package com.renaissancerentals.persistence.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.lang.NonNull;

import com.renaissancerentals.persistence.converter.AdditionalInfoReadingConverter;
import com.renaissancerentals.persistence.converter.AdditionalInfoWritingConverter;

@Configuration
@EnableJdbcRepositories(basePackages = "com.renaissancerentals.persistence.dao")
public class PersistenceAutoConfiguration extends AbstractJdbcConfiguration {
    @Override
    @NonNull
    public JdbcCustomConversions jdbcCustomConversions(){
        return new JdbcCustomConversions(
                List.of(new AdditionalInfoWritingConverter(),new AdditionalInfoReadingConverter()));
    }
}
