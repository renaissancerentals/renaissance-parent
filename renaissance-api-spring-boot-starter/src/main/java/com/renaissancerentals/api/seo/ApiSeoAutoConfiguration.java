package com.renaissancerentals.api.seo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renaissancerentals.api.repository.JobVacancyRepository;
import com.renaissancerentals.api.repository.SubletRepository;
import com.renaissancerentals.api.service.FloorplanService;
import com.renaissancerentals.api.service.PropertyService;
import com.renaissancerentals.foundation.seo.SeoAutoConfiguration;
import com.renaissancerentals.foundation.seo.SeoDataSource;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

/**
 * Sites that carry the API (and so a database) read their SEO data from it directly instead of calling a data host
 * over HTTP. Runs before the foundation auto-configuration so that it wins over the remote data source.
 */
@AutoConfiguration(before = SeoAutoConfiguration.class)
@ConditionalOnProperty(prefix = "renaissancerentals.seo", name = "canonical-origin")
public class ApiSeoAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(SeoDataSource.class)
    @ConditionalOnBean({
        PropertyService.class,
        FloorplanService.class,
        SubletRepository.class,
        JobVacancyRepository.class
    })
    public SeoDataSource seoLocalDataSource(
            PropertyService propertyService,
            FloorplanService floorplanService,
            SubletRepository subletRepository,
            JobVacancyRepository jobVacancyRepository,
            ObjectMapper mapper) {
        return new LocalSeoDataSource(
                propertyService, floorplanService, subletRepository, jobVacancyRepository, mapper);
    }
}
