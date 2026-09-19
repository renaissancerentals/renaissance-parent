package com.renaissancerentals.persistence.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.renaissancerentals.persistence.entity.WebSpecialEntity;
import com.renaissancerentals.persistence.support.PostgresIntegrationTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class WebSpecialDaoIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private WebSpecialDao webSpecialDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void seed() {
        jdbcTemplate.update("DELETE FROM web_special");
        jdbcTemplate.update("DELETE FROM floorplan");
        jdbcTemplate.update("DELETE FROM property");

        jdbcTemplate.update("INSERT INTO property (id, name) VALUES ('p-1', 'Property One')");
        jdbcTemplate.update("INSERT INTO floorplan (id, name, property_id) VALUES ('f-1', 'Floorplan One', 'p-1')");

        jdbcTemplate.update("INSERT INTO web_special (start_date, end_date, description, floorplan_id) "
                + "VALUES (CURRENT_DATE - 1, CURRENT_DATE + 1, 'Active', 'f-1')");
        jdbcTemplate.update("INSERT INTO web_special (start_date, end_date, description, floorplan_id) "
                + "VALUES (CURRENT_DATE - 10, CURRENT_DATE - 5, 'Expired', 'f-1')");
    }

    @Test
    void findAllByFloorplanIdReturnsAllRegardlessOfDate() {
        List<WebSpecialEntity> result = webSpecialDao.findAllByFloorplanId("f-1");

        assertThat(result).hasSize(2);
    }

    @Test
    void findActiveByFloorplanIdReturnsOnlyEntriesWithinDateWindow() {
        List<WebSpecialEntity> result = webSpecialDao.findActiveByFloorplanId("f-1");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getDescription()).isEqualTo("Active");
    }
}
