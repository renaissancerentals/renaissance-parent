package com.renaissancerentals.persistence.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.renaissancerentals.persistence.entity.UnitEntity;
import com.renaissancerentals.persistence.support.PostgresIntegrationTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class UnitDaoIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private UnitDao unitDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void seed() {
        jdbcTemplate.update("DELETE FROM unit");
        jdbcTemplate.update("DELETE FROM floorplan");
        jdbcTemplate.update("DELETE FROM property");

        jdbcTemplate.update("INSERT INTO property (id, name) VALUES ('p-active', 'Active Property')");
        jdbcTemplate.update(
                "INSERT INTO property (id, name, active) VALUES ('p-inactive', 'Inactive Property', false)");

        jdbcTemplate.update(
                "INSERT INTO floorplan (id, name, property_id) VALUES ('f-active', 'Active Floorplan', 'p-active')");
        jdbcTemplate.update(
                "INSERT INTO floorplan (id, name, property_id, active) VALUES ('f-inactive', 'Inactive Floorplan', 'p-inactive', false)");

        jdbcTemplate.update("INSERT INTO unit (id, floorplan_id) VALUES ('u-active', 'f-active')");
        jdbcTemplate.update(
                "INSERT INTO unit (id, floorplan_id, active) VALUES ('u-inactive-unit', 'f-active', false)");
        jdbcTemplate.update("INSERT INTO unit (id, floorplan_id) VALUES ('u-inactive-floorplan', 'f-inactive')");
    }

    @Test
    void findAllActiveRequiresUnitFloorplanAndPropertyAllActive() {
        List<UnitEntity> result = unitDao.findAllActive();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo("u-active");
    }

    @Test
    void findAllByFloorplanIdReturnsAllUnitsRegardlessOfActiveState() {
        List<UnitEntity> result = unitDao.findAllByFloorplanId("f-active");

        assertThat(result).hasSize(2);
    }
}
