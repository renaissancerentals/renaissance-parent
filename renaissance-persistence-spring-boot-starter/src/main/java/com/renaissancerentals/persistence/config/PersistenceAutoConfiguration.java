package com.renaissancerentals.persistence.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renaissancerentals.persistence.converter.AdditionalInfoConverter;
import com.renaissancerentals.persistence.converter.LinkedImageRegionsConverter;
import com.renaissancerentals.persistence.converter.OwnerDataConverters;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jdbc.core.convert.JdbcCustomConversions;
import org.springframework.data.jdbc.repository.config.AbstractJdbcConfiguration;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.lang.NonNull;

@Configuration
@EnableJdbcRepositories(basePackages = "com.renaissancerentals.persistence.dao")
@RequiredArgsConstructor
public class PersistenceAutoConfiguration extends AbstractJdbcConfiguration {
    private final ObjectMapper objectMapper;

    @Override
    @NonNull
    public JdbcCustomConversions jdbcCustomConversions() {
        return new JdbcCustomConversions(List.of(
                new AdditionalInfoConverter.AdditionalInfoWritingConverter(objectMapper),
                new AdditionalInfoConverter.AdditionalInfoReadingConverter(objectMapper),
                new LinkedImageRegionsConverter.LinkedImageReadingConverter(objectMapper),
                new LinkedImageRegionsConverter.LinkedImageWritingConverter(objectMapper),
                new OwnerDataConverters.OwnerDataReadingConverter(objectMapper),
                new OwnerDataConverters.OwnerDataWritingConverter(objectMapper)));
    }
}
