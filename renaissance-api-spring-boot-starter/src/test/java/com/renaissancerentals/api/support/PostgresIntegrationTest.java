package com.renaissancerentals.api.support;

import com.renaissancerentals.api.repository.ContactRepository;
import com.renaissancerentals.api.repository.JobVacancyRepository;
import com.renaissancerentals.api.repository.SubletRepository;
import com.renaissancerentals.api.repository.UnitRepository;
import org.junit.jupiter.api.Tag;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Base class for api-starter's PropertyRepository/FloorplanRepository tests
 * (the SqlBuilder-based joins every public-facing app flows through).
 *
 * Scoped to just the repository + mapper packages rather than the full
 * ApiAutoConfiguration, which pulls in mail/text/asset config needing live
 * third-party credentials unrelated to verifying these queries against
 * Postgres. Liquibase runs for real via the persistence starter's changelog
 * (transitively on the classpath here), same as production.
 *
 * Singleton container pattern (see the persistence-starter/renaissance-admin
 * copies of this class for why): one Postgres container for the whole test
 * run rather than one per test class, to avoid resource contention on a
 * memory-capped Docker daemon.
 */
@SpringBootTest(classes = PostgresIntegrationTest.TestApplication.class)
@Tag("integration")
public abstract class PostgresIntegrationTest {

    private static final PostgreSQLContainer<?> POSTGRES;

    static {
        POSTGRES = new PostgreSQLContainer<>(DockerImageName.parse("postgres:18"));
        POSTGRES.start();
    }

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.liquibase.change-log", () -> "classpath:liquibase-changeLog.sql");
    }

    @SpringBootApplication
    @ComponentScan(
            basePackages = {"com.renaissancerentals.api.repository", "com.renaissancerentals.api.domain.template"},
            excludeFilters =
                    @ComponentScan.Filter(
                            type = FilterType.ASSIGNABLE_TYPE,
                            classes = {
                                ContactRepository.class,
                                JobVacancyRepository.class,
                                SubletRepository.class,
                                UnitRepository.class
                            }))
    static class TestApplication {}
}
