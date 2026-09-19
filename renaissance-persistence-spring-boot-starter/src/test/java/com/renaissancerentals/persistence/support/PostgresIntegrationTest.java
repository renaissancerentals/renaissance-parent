package com.renaissancerentals.persistence.support;

import org.junit.jupiter.api.Tag;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

/**
 * Base class for DAO/repository tests that need a real Postgres instance.
 * Pin the tag to whatever version you're validating an upgrade against
 * (bump alongside the Heroku/local Docker upgrade, then bump again once 18 is the new baseline).
 *
 * Deliberately NOT using @Testcontainers/@Container (one container per test
 * class): on a memory-capped Docker daemon, starting/tearing down a fresh
 * Postgres + full Liquibase migration for every test class causes connection
 * timeouts under load. Instead this is the "singleton container" pattern -
 * one container for the whole JVM/test run, started once in a static
 * initializer and left running; Testcontainers' Ryuk reaper cleans it up
 * when the JVM exits.
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
        // Same changelog production runs (renaissance-persistence-spring-boot-starter/src/main/resources),
        // so the test schema can't drift from what's actually deployed.
        registry.add("spring.liquibase.change-log", () -> "classpath:liquibase-changeLog.sql");
    }

    @SpringBootApplication
    static class TestApplication {}
}
