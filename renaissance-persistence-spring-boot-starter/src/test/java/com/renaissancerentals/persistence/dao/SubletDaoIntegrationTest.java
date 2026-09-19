package com.renaissancerentals.persistence.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.renaissancerentals.persistence.entity.SubletEntity;
import com.renaissancerentals.persistence.support.PostgresIntegrationTest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class SubletDaoIntegrationTest extends PostgresIntegrationTest {

    private static final String SUBLET_COLUMNS =
            "asset_key, first_name, last_name, email, bedroom, available_bedrooms, available_from, "
                    + "available_to, rent, pets_allowed, utilities_included, address, zipcode, cover_image, "
                    + "title, created_date, active, approved";

    @Autowired
    private SubletDao subletDao;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void seed() {
        jdbcTemplate.update("DELETE FROM sublet");

        insertSublet("active-1", true, true, "", "2026-06-01", "2026-01-01T00:00:00");
        insertSublet("expired-by-date", true, false, "img.jpg", "2020-01-01", "2026-01-01T00:00:00");
        insertSublet("inactive-old", false, false, "img2.jpg", "2026-06-01", "2020-01-01T00:00:00");
    }

    private void insertSublet(
            String assetKey,
            boolean active,
            boolean approved,
            String coverImage,
            String availableTo,
            String createdDate) {
        jdbcTemplate.update(
                "INSERT INTO sublet (" + SUBLET_COLUMNS + ") VALUES "
                        + "(?, 'First', 'Last', 'test@example.com', 2, 2, '2026-01-01', ?::date, 1000, true, true, "
                        + "'123 Main St', '47401', ?, 'Title', ?::timestamp, ?, ?)",
                assetKey,
                availableTo,
                coverImage,
                createdDate,
                active,
                approved);
    }

    @Test
    void findByActiveTrueReturnsOnlyActiveSublets() {
        List<SubletEntity> result = subletDao.findByActiveTrue();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(SubletEntity::getAssetKey).doesNotContain("inactive-old");
    }

    @Test
    void findByActiveTrueAndApprovedTrueRequiresBoth() {
        List<SubletEntity> result = subletDao.findByActiveTrueAndApprovedTrue();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getAssetKey()).isEqualTo("active-1");
    }

    @Test
    void findOneByAssetKeyReturnsMatchingSublet() {
        Optional<SubletEntity> result = subletDao.findOneByAssetKey("active-1");

        assertThat(result).isPresent();
    }

    @Test
    void findByCoverImageEmptyReturnsSubletsMissingAnImage() {
        List<SubletEntity> result = subletDao.findByCoverImageEmpty();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getAssetKey()).isEqualTo("active-1");
    }

    @Test
    void findExpiredMatchesEitherPastAvailableToOrOldCreatedDate() {
        List<SubletEntity> result = subletDao.findExpired(LocalDate.of(2025, 1, 1), LocalDateTime.of(2021, 1, 1, 0, 0));

        assertThat(result).extracting(SubletEntity::getAssetKey).contains("expired-by-date", "inactive-old");
    }

    @Test
    void findExpiredInactiveRequiresInactiveAndOldCreatedDate() {
        List<SubletEntity> result = subletDao.findExpiredInactive(LocalDateTime.of(2021, 1, 1, 0, 0));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getAssetKey()).isEqualTo("inactive-old");
    }
}
