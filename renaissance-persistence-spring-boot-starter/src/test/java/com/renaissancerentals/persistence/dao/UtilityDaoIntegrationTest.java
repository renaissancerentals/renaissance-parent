package com.renaissancerentals.persistence.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.renaissancerentals.persistence.entity.UtilityEntity;
import com.renaissancerentals.persistence.support.PostgresIntegrationTest;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class UtilityDaoIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private UtilityDao utilityDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void seed() {
        jdbcTemplate.update("DELETE FROM utility");
        jdbcTemplate.update("DELETE FROM floorplan");
        jdbcTemplate.update("DELETE FROM property");

        jdbcTemplate.update("INSERT INTO property (id, name) VALUES ('p-1', 'Property One')");
        jdbcTemplate.update("INSERT INTO floorplan (id, name, property_id) VALUES ('f-1', 'Floorplan One', 'p-1')");
        jdbcTemplate.update("INSERT INTO floorplan (id, name, property_id) VALUES ('f-2', 'Floorplan Two', 'p-1')");

        jdbcTemplate.update(
                "INSERT INTO utility (id, name, type, floorplan_id) VALUES (1, 'Electric', 'ELECTRIC', 'f-1')");
        jdbcTemplate.update("INSERT INTO utility (id, name, type, floorplan_id) VALUES (2, 'Water', 'WATER', 'f-1')");
        jdbcTemplate.update(
                "INSERT INTO utility (id, name, type, floorplan_id) VALUES (3, 'Electric', 'ELECTRIC', 'f-2')");
    }

    @Test
    void findByFloorplanIdAndNameAndTypeNarrowsToExactMatch() {
        List<UtilityEntity> result = utilityDao.findByFloorplanIdAndNameAndType("f-1", "Electric", "ELECTRIC");

        assertThat(result).hasSize(1);
    }

    @Test
    void findByFloorplanIdReturnsAllUtilitiesForFloorplan() {
        List<UtilityEntity> result = utilityDao.findByFloorplanId("f-1");

        assertThat(result).hasSize(2);
    }

    @Test
    void findDistinctNamesDeduplicatesAcrossFloorplans() {
        Set<String> result = utilityDao.findDistinctNames();

        assertThat(result).containsExactlyInAnyOrder("Electric", "Water");
    }

    @Test
    void deleteByFloorplanIdRemovesOnlyThatFloorplansUtilities() {
        utilityDao.deleteByFloorplanId("f-1");

        assertThat(utilityDao.findByFloorplanId("f-1")).isEmpty();
        assertThat(utilityDao.findByFloorplanId("f-2")).hasSize(1);
    }
}
