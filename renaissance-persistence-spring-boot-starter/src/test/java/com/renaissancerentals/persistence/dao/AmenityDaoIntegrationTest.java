package com.renaissancerentals.persistence.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.renaissancerentals.persistence.entity.AmenityEntity;
import com.renaissancerentals.persistence.support.PostgresIntegrationTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class AmenityDaoIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private AmenityDao amenityDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void seed() {
        jdbcTemplate.update("DELETE FROM amenity");
        jdbcTemplate.update("DELETE FROM floorplan");
        jdbcTemplate.update("DELETE FROM property");

        jdbcTemplate.update("INSERT INTO property (id, name) VALUES ('p-1', 'Property One')");
        jdbcTemplate.update("INSERT INTO floorplan (id, name, property_id) VALUES ('f-1', 'Floorplan One', 'p-1')");

        jdbcTemplate.update(
                "INSERT INTO amenity (id, name, type, floorplan_id) VALUES (1, 'Pool', 'COMMUNITY', 'f-1')");
        jdbcTemplate.update(
                "INSERT INTO amenity (id, name, type, floorplan_id) VALUES (2, 'Pool', 'COMMUNITY', 'f-1')");
        jdbcTemplate.update("INSERT INTO amenity (id, name, type, floorplan_id) VALUES (3, 'Balcony', 'UNIT', 'f-1')");
    }

    @Test
    void findByFloorplanIdReturnsMatchingAmenities() {
        List<AmenityEntity> result = amenityDao.findByFloorplanId("f-1");

        assertThat(result).hasSize(3);
    }

    @Test
    void findDistinctTypeAndNameDeduplicates() {
        List<AmenityEntity> result = amenityDao.findDistinctTypeAndName();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(AmenityEntity::getName).containsExactlyInAnyOrder("Pool", "Balcony");
    }
}
