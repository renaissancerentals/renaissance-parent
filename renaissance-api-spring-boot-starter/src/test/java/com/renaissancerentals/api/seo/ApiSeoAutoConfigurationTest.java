package com.renaissancerentals.api.seo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renaissancerentals.api.repository.JobVacancyRepository;
import com.renaissancerentals.api.repository.SubletRepository;
import com.renaissancerentals.api.service.FloorplanService;
import com.renaissancerentals.api.service.PropertyService;
import com.renaissancerentals.foundation.seo.RemoteSeoDataSource;
import com.renaissancerentals.foundation.seo.SeoAutoConfiguration;
import com.renaissancerentals.foundation.seo.SeoDataSource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

class ApiSeoAutoConfigurationTest {

    private final WebApplicationContextRunner runner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ApiSeoAutoConfiguration.class, SeoAutoConfiguration.class))
            .withBean(ObjectMapper.class, ObjectMapper::new)
            .withPropertyValues("renaissancerentals.seo.canonical-origin=https://www.renaissancerentals.com");

    @Test
    void aSiteWithTheApiReadsItsOwnDatabase() {
        runner.withBean(PropertyService.class, () -> mock(PropertyService.class))
                .withBean(FloorplanService.class, () -> mock(FloorplanService.class))
                .withBean(SubletRepository.class, () -> mock(SubletRepository.class))
                .withBean(JobVacancyRepository.class, () -> mock(JobVacancyRepository.class))
                .run(context -> assertThat(context)
                        .hasSingleBean(SeoDataSource.class)
                        .getBean(SeoDataSource.class)
                        .isInstanceOf(LocalSeoDataSource.class));
    }

    @Test
    void withoutTheApiBeansItFallsBackToTheRemoteDataSource() {
        runner.run(context -> assertThat(context).getBean(SeoDataSource.class).isInstanceOf(RemoteSeoDataSource.class));
    }
}
