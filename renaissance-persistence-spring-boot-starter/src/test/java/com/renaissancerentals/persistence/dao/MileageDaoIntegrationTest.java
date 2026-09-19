package com.renaissancerentals.persistence.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.renaissancerentals.persistence.entity.MileageEntity;
import com.renaissancerentals.persistence.support.PostgresIntegrationTest;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class MileageDaoIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private MileageDao mileageDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void seed() {
        jdbcTemplate.update("DELETE FROM mileage");

        jdbcTemplate.update(
                "INSERT INTO mileage (drive_date, employee, starting_mileage) VALUES ('2026-01-05', 'alice', 100)");
        jdbcTemplate.update(
                "INSERT INTO mileage (drive_date, employee, starting_mileage) VALUES ('2026-01-20', 'bob', 200)");
    }

    @Test
    void findAllByEmployeeReturnsOnlyThatEmployeesEntries() {
        List<MileageEntity> result = mileageDao.findAllByEmployee("alice");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getStartingMileage()).isEqualTo(100);
    }

    @Test
    void findBetweenReturnsEntriesAcrossEmployeesInRange() {
        List<MileageEntity> result = mileageDao.findBetween(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 1, 10));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getEmployee()).isEqualTo("alice");
    }
}
