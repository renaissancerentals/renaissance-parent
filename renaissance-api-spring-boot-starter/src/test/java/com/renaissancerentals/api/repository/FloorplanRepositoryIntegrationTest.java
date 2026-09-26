package com.renaissancerentals.api.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.renaissancerentals.api.domain.projection.FloorplanDetails;
import com.renaissancerentals.api.domain.projection.FloorplanSpotlight;
import com.renaissancerentals.api.support.PostgresIntegrationTest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class FloorplanRepositoryIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private FloorplanRepository floorplanRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void seed() {
        jdbcTemplate.update("DELETE FROM amenity");
        jdbcTemplate.update("DELETE FROM web_special");
        jdbcTemplate.update("DELETE FROM unit");
        jdbcTemplate.update("DELETE FROM floorplan");
        jdbcTemplate.update("DELETE FROM property");

        jdbcTemplate.update(
                "INSERT INTO property (id, name, address, zipcode) VALUES ('p-1', 'Property One', '1 Main St', '47401')");

        jdbcTemplate.update("INSERT INTO floorplan (id, name, property_id, style, featured, active) "
                + "VALUES ('f-active', 'Active Floorplan', 'p-1', 'STUDIO', true, true)");
        jdbcTemplate.update("INSERT INTO floorplan (id, name, property_id, style, active) "
                + "VALUES ('f-garage', 'Garage', 'p-1', 'GARAGE', true)");
        jdbcTemplate.update("INSERT INTO floorplan (id, name, property_id, style, active) "
                + "VALUES ('f-inactive', 'Inactive Floorplan', 'p-1', 'STUDIO', false)");

        jdbcTemplate.update("INSERT INTO unit (id, floorplan_id, rent, address, zipcode) "
                + "VALUES ('u-1', 'f-active', 950.0, '1 Main St Unit 1', '47401')");
        jdbcTemplate.update("INSERT INTO web_special (start_date, end_date, description, floorplan_id) "
                + "VALUES (CURRENT_DATE - 1, CURRENT_DATE + 1, 'Move-in special', 'f-active')");
        jdbcTemplate.update(
                "INSERT INTO amenity (id, name, type, floorplan_id) VALUES (1, 'Pool', 'COMMUNITY', 'f-active')");
    }

    @Test
    void getActiveFloorplansDetailsExcludesGarageAndInactiveStyles() {
        List<FloorplanDetails> result = floorplanRepository.getActiveFloorplansDetails();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo("f-active");
    }

    @Test
    void getFloorplanDetailsIncludesUnitsWebSpecialsAndAmenities() {
        Optional<FloorplanDetails> result = floorplanRepository.getFloorplanDetails("f-active");

        assertThat(result).isPresent();
        assertThat(result.get().getUnits()).hasSize(1);
        assertThat(result.get().getWebSpecials()).hasSize(1);
        assertThat(result.get().getAmenities()).hasSize(1);
    }

    @Test
    void getFloorplanDetailsForPropertyFiltersByProperty() {
        List<FloorplanDetails> result = floorplanRepository.getFloorplanDetailsForProperty("p-1");

        assertThat(result).hasSize(1);
    }

    @Test
    void getFloorplanDetailsExcludesInactiveFloorplan() {
        Optional<FloorplanDetails> result = floorplanRepository.getFloorplanDetails("f-inactive");

        assertThat(result).isEmpty();
    }

    @Test
    void getFeaturedSpotlightsRequiresActiveAndFeatured() {
        List<FloorplanSpotlight> result = floorplanRepository.getFeaturedSpotlights();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getProperty().getId()).isEqualTo("p-1");
        assertThat(result.getFirst().getUnits()).hasSize(1);
    }

    @Test
    void getFloorplanSpotlightReturnsSingleResult() {
        Optional<FloorplanSpotlight> result = floorplanRepository.getFloorplanSpotlight("f-active");

        assertThat(result).isPresent();
    }

    @Test
    void getFloorplanSpotlightExcludesInactiveFloorplan() {
        Optional<FloorplanSpotlight> result = floorplanRepository.getFloorplanSpotlight("f-inactive");

        assertThat(result).isEmpty();
    }
}
