package com.renaissancerentals.data.api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import com.renaissancerentals.persistence.entity.JobVacancyEntity;
import com.renaissancerentals.persistence.entity.SubletEntity;

@Configuration
public class RestConfig implements RepositoryRestConfigurer {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config,CorsRegistry cors){
        // Expose IDs for all entities
        config.exposeIdsFor(JobVacancyEntity.class,SubletEntity.class
        // add other entities here
        );
    }
}
