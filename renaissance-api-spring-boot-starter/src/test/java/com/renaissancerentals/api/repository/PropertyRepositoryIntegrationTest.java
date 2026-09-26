package com.renaissancerentals.api.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.renaissancerentals.api.domain.TeamMember;
import com.renaissancerentals.api.domain.projection.PropertyDetails;
import com.renaissancerentals.api.domain.projection.PropertyListing;
import com.renaissancerentals.api.domain.projection.PropertySummary;
import com.renaissancerentals.api.support.PostgresIntegrationTest;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

class PropertyRepositoryIntegrationTest extends PostgresIntegrationTest {

    @Autowired
    private PropertyRepository propertyRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void seed() {
        jdbcTemplate.update("DELETE FROM property_bus_route");
        jdbcTemplate.update("DELETE FROM property_amenity");
        jdbcTemplate.update("DELETE FROM team_member_property");
        jdbcTemplate.update("DELETE FROM team_member");
        jdbcTemplate.update("DELETE FROM web_special");
        jdbcTemplate.update("DELETE FROM unit");
        jdbcTemplate.update("DELETE FROM floorplan");
        jdbcTemplate.update("DELETE FROM property");
        jdbcTemplate.update("DELETE FROM leasing_office");

        jdbcTemplate.update("INSERT INTO leasing_office (id, name) VALUES ('lo-1', 'Main Office')");
        jdbcTemplate.update(
                "INSERT INTO property (id, name, address, zipcode, email, phone, leasing_office_id) "
                        + "VALUES ('p-yearly', 'Yearly Property', '1 Main St', '47401', 'p@example.com', '8125551234', 'lo-1')");
        jdbcTemplate.update("INSERT INTO property (id, name, lease_type, active, leasing_office_id) "
                + "VALUES ('p-inactive', 'Inactive Property', 'YEARLY', false, 'lo-1')");

        jdbcTemplate.update("INSERT INTO floorplan (id, name, property_id, style, active) "
                + "VALUES ('f-1', 'Floorplan One', 'p-yearly', 'STUDIO', true)");
        jdbcTemplate.update("INSERT INTO unit (id, floorplan_id, rent, active) VALUES ('u-1', 'f-1', 900.0, true)");
        jdbcTemplate.update("INSERT INTO floorplan (id, name, property_id, style, active) "
                + "VALUES ('f-inactive-property', 'Floorplan On Inactive Property', 'p-inactive', 'STUDIO', true)");

        jdbcTemplate.update("INSERT INTO team_member (id, name, job_title) VALUES (1, 'Alice', 'Brand Manager')");
        jdbcTemplate.update(
                "INSERT INTO team_member_property (id, property_id, team_member_id) VALUES (1, 'p-yearly', 1)");

        jdbcTemplate.update("INSERT INTO property_bus_route (id, bus_route, bus_route_link, property_id) "
                + "VALUES (1, 'Route 5', 'https://example.com/route5', 'p-yearly')");
    }

    @Test
    void getPropertyManagerReturnsBrandManagerForProperty() {
        Optional<TeamMember> result = propertyRepository.getPropertyManager("p-yearly");

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Alice");
    }

    @Test
    void getPropertyTeamMembersReturnsAllAssignedMembers() {
        List<TeamMember> result = propertyRepository.getPropertyTeamMembers("p-yearly");

        assertThat(result).hasSize(1);
    }

    @Test
    void getPropertyReturnsDetailsWithLeasingOfficeAmenitiesBusRoutesTeamAndFloorplans() {
        Optional<PropertyDetails> result = propertyRepository.getProperty("p-yearly");

        assertThat(result).isPresent();
        assertThat(result.get().getBusRoutes()).hasSize(1);
        assertThat(result.get().getTeamMembers()).hasSize(1);
        assertThat(result.get().getFloorplans()).hasSize(1);
    }

    @Test
    void getPropertyReturnsEmptyForInactiveProperty() {
        assertThat(propertyRepository.getProperty("p-inactive")).isEmpty();
    }

    @Test
    void getPropertyListingsOnlyIncludesActiveYearlyNonGarageFloorplansWithUnits() {
        List<PropertyListing> result = propertyRepository.getPropertyListings();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo("p-yearly");
    }

    @Test
    void getPropertyListingReturnsListingForSpecificProperty() {
        Optional<PropertyListing> result = propertyRepository.getPropertyListing("p-yearly");

        assertThat(result).isPresent();
    }

    @Test
    void getPropertyListingReturnsEmptyForInactiveProperty() {
        assertThat(propertyRepository.getPropertyListing("p-inactive")).isEmpty();
    }

    @Test
    void getPropertySummaryForPropertyIncludesBusRoutes() {
        Optional<PropertySummary> result = propertyRepository.getPropertySummaryForProperty("p-yearly");

        assertThat(result).isPresent();
        assertThat(result.get().getBusRoutes()).hasSize(1);
    }

    @Test
    void getPropertySummaryForPropertyReturnsEmptyForInactiveProperty() {
        assertThat(propertyRepository.getPropertySummaryForProperty("p-inactive"))
                .isEmpty();
    }

    @Test
    void getPropertySummaryForFloorplanJoinsThroughFloorplan() {
        Optional<PropertySummary> result = propertyRepository.getPropertySummaryForFloorplan("f-1");

        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo("p-yearly");
    }

    @Test
    void getPropertySummaryForFloorplanReturnsEmptyWhenPropertyInactive() {
        assertThat(propertyRepository.getPropertySummaryForFloorplan("f-inactive-property"))
                .isEmpty();
    }
}
