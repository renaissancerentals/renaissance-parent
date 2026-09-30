package com.renaissancerentals.foundation.seo;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.assertj.AssertableWebApplicationContext;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

class SeoAutoConfigurationTest {

    @Configuration
    static class Json {
        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

    private final WebApplicationContextRunner runner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SeoAutoConfiguration.class))
            .withUserConfiguration(Json.class);

    @Test
    void isOffUntilACanonicalOriginIsConfigured() {
        runner.run(
                context -> assertThat(context).doesNotHaveBean(SeoService.class).doesNotHaveBean(SeoShellFilter.class));
    }

    @Test
    void wiresEverythingAndDefaultsToTheRemoteDataSource() {
        runner.withPropertyValues(
                        "renaissancerentals.seo.canonical-origin=https://www.highgrovebloomington.com/",
                        "renaissancerentals.seo.owned-property-id=high-grove",
                        "renaissancerentals.seo.pages[0].path=/contact",
                        "renaissancerentals.seo.pages[0].title=Contact",
                        "renaissancerentals.seo.organization.telephone=8123332280")
                .run((AssertableWebApplicationContext context) -> {
                    assertThat(context).hasSingleBean(SeoService.class).hasSingleBean(SeoController.class);
                    assertThat(context).getBean(SeoDataSource.class).isInstanceOf(RemoteSeoDataSource.class);
                    var properties = context.getBean(SeoProperties.class);
                    assertThat(properties.canonicalOrigin()).isEqualTo("https://www.highgrovebloomington.com");
                    assertThat(properties.pages()).hasSize(1);
                    assertThat(properties.dataBaseUrls())
                            .containsExactly("https://www.renaissancerentals.com", "https://www.scholarsrooftop.com");
                    assertThat(properties.propertySites()).containsKey("high-grove");
                    assertThat(properties.preferPropertySiteCanonical()).isTrue();
                    assertThat(context.getBeansOfType(SeoPageContributor.class)).hasSize(3);
                });
    }

    @Test
    void aSiteWithADatabaseCanProvideItsOwnDataSource() {
        runner.withPropertyValues("renaissancerentals.seo.canonical-origin=https://www.renaissancerentals.com")
                .withBean(SeoDataSource.class, () -> new SeoTestSupport.FakeData())
                .run(context ->
                        assertThat(context).getBean(SeoDataSource.class).isInstanceOf(SeoTestSupport.FakeData.class));
    }
}
