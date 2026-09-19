package com.renaissancerentals.persistence.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.renaissancerentals.persistence.entity.PropertyCheckEntity;
import com.renaissancerentals.persistence.support.PostgresIntegrationTest;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class PropertyCheckDaoIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private PropertyCheckDao propertyCheckDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void seed() {
        jdbcTemplate.update("DELETE FROM property_check");

        jdbcTemplate.update("INSERT INTO property_check (employee, start_date, start_time, start_asset_id) "
                + "VALUES ('alice', '2026-01-05', '09:00', 'asset-1')");
        jdbcTemplate.update("INSERT INTO property_check (employee, start_date, start_time, start_asset_id) "
                + "VALUES ('alice', '2026-01-10', '10:00', 'asset-2')");
        jdbcTemplate.update("INSERT INTO property_check (employee, start_date, start_time, start_asset_id) "
                + "VALUES ('bob', '2026-01-05', '11:00', 'asset-3')");
    }

    @Test
    void findAllByEmployeeReturnsOnlyThatEmployeesChecks() {
        List<PropertyCheckEntity> result = propertyCheckDao.findAllByEmployee("alice");

        assertThat(result).hasSize(2);
    }

    @Test
    void findAllByEmployeeAndStartDateNarrowsToExactDate() {
        List<PropertyCheckEntity> result =
                propertyCheckDao.findAllByEmployeeAndStartDate("alice", LocalDate.of(2026, 1, 5));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getStartAssetId()).isEqualTo("asset-1");
    }

    @Test
    void findBetweenReturnsChecksAcrossAllEmployeesInRange() {
        List<PropertyCheckEntity> result =
                propertyCheckDao.findBetween(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 7));

        assertThat(result).hasSize(2);
    }
}
