package com.renaissancerentals.persistence.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.renaissancerentals.persistence.entity.ShortTermFloorplanEntity;
import com.renaissancerentals.persistence.support.PostgresIntegrationTest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class ShortTermFloorplanDaoIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private ShortTermFloorplanDao shortTermFloorplanDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void seed() {
        jdbcTemplate.update("DELETE FROM short_term_floorplan");
        jdbcTemplate.update("DELETE FROM floorplan");
        jdbcTemplate.update("DELETE FROM property");

        jdbcTemplate.update("INSERT INTO property (id, name) VALUES ('p-1', 'Property One')");
        jdbcTemplate.update("INSERT INTO floorplan (id, name, property_id) VALUES ('f-1', 'Floorplan One', 'p-1')");
        jdbcTemplate.update("INSERT INTO short_term_floorplan (square_foot, floorplan_id) VALUES (750, 'f-1')");
    }

    @Test
    void findOneByFloorplanIdReturnsMatchingRow() {
        Optional<ShortTermFloorplanEntity> result = shortTermFloorplanDao.findOneByFloorplanId("f-1");

        assertThat(result).isPresent();
        assertThat(result.get().getSquareFoot()).isEqualTo(750);
    }

    @Test
    void findByPropertyIdJoinsThroughFloorplan() {
        List<ShortTermFloorplanEntity> result = shortTermFloorplanDao.findByPropertyId("p-1");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getFloorplanId()).isEqualTo("f-1");
    }
}
