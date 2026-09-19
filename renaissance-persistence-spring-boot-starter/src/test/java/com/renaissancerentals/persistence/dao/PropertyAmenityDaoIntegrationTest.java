package com.renaissancerentals.persistence.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.renaissancerentals.persistence.entity.PropertyAmenityEntity;
import com.renaissancerentals.persistence.support.PostgresIntegrationTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class PropertyAmenityDaoIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private PropertyAmenityDao propertyAmenityDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void seed() {
        jdbcTemplate.update("DELETE FROM property_amenity");
        jdbcTemplate.update("DELETE FROM property");

        jdbcTemplate.update("INSERT INTO property (id, name) VALUES ('p-1', 'Property One')");

        jdbcTemplate.update(
                "INSERT INTO property_amenity (id, name, type, property_id) VALUES (1, 'Pool', 'COMMUNITY', 'p-1')");
        jdbcTemplate.update(
                "INSERT INTO property_amenity (id, name, type, property_id) VALUES (2, 'Pool', 'COMMUNITY', 'p-1')");
        jdbcTemplate.update(
                "INSERT INTO property_amenity (id, name, type, property_id) VALUES (3, 'Gym', 'COMMUNITY', 'p-1')");
    }

    @Test
    void findDistinctTypeAndNameDeduplicates() {
        List<PropertyAmenityEntity> result = propertyAmenityDao.findDistinctTypeAndName();

        assertThat(result).hasSize(2);
    }

    @Test
    void findByPropertyIdReturnsAllRowsForThatProperty() {
        List<PropertyAmenityEntity> result = propertyAmenityDao.findByPropertyId("p-1");

        assertThat(result).hasSize(3);
    }
}
