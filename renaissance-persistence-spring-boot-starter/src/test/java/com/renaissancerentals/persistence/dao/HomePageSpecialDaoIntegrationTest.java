package com.renaissancerentals.persistence.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.renaissancerentals.persistence.entity.HomePageSpecialEntity;
import com.renaissancerentals.persistence.support.PostgresIntegrationTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class HomePageSpecialDaoIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private HomePageSpecialDao homePageSpecialDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void seed() {
        jdbcTemplate.update("DELETE FROM home_page_special");

        jdbcTemplate.update("INSERT INTO home_page_special (description, image, start_date, end_date) "
                + "VALUES ('Active special', 'active.jpg', CURRENT_DATE - 1, CURRENT_DATE + 1)");
        jdbcTemplate.update("INSERT INTO home_page_special (description, image, start_date, end_date) "
                + "VALUES ('Expired special', 'expired.jpg', CURRENT_DATE - 10, CURRENT_DATE - 5)");
        jdbcTemplate.update("INSERT INTO home_page_special (description, image, start_date, end_date) "
                + "VALUES ('Future special', 'future.jpg', CURRENT_DATE + 5, CURRENT_DATE + 10)");
    }

    @Test
    void findAllActiveReturnsOnlySpecialsWithinTheCurrentDateWindow() {
        List<HomePageSpecialEntity> result = homePageSpecialDao.findAllActive();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getDescription()).isEqualTo("Active special");
    }
}
