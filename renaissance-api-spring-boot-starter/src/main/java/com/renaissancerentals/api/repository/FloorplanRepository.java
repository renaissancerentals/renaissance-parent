package com.renaissancerentals.api.repository;

import com.renaissancerentals.api.domain.projection.FloorplanDetails;
import com.renaissancerentals.api.domain.projection.FloorplanSpotlight;
import com.renaissancerentals.api.repository.helper.SqlBuilder;
import com.renaissancerentals.api.repository.mapper.FloorplanDetailsExtractor;
import com.renaissancerentals.api.repository.mapper.FloorplanSpotlightExtractor;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class FloorplanRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    private final FloorplanDetailsExtractor floorplanDetailsExtractor;
    private final FloorplanSpotlightExtractor floorplanSpotlightExtractor;

    private static final String FLOORPLAN_DETAILS_SQL =
            """
            SELECT f.id,
                   f.name,
                   f.bedroom,
                   f.bathroom,
                   f.style,
                   f.special_rent,
                   f.special_rent_start_date,
                   f.special_rent_end_date,
                   f.address,
                   f.zipcode,
                   f.featured,
                   f.green_certified,
                   f.video_tour_link,
                   f.three_sixty_video_tour_link,
                   f.virtual_tour_link,
                   f.photo,
                   f.cover_image,
                   f.floor_plan_folder_id,
                   f.photos_folder_id,
                   f.photos_count,
                   f.description,
                   f.vanity_link,
                   f.html_title,
                   f.meta_description,
                   f.conversion_tracking_id1,
                   f.conversion_tracking_id2,
                   f.custom_code,
                   f.highlights,
                   u.id          AS unit_id,
                   u.square_foot,
                   u.allowed_pet,
                   u.rent,
                   u.discounted_rent,
                   u.deposit,
                   u.garages,
                   u.move_in_date,
                   u.availability_extension_months,
                   u.furnished,
                   w.id          AS web_special_id,
                   w.description AS web_special_description,
                   w.start_date  AS web_special_start_date,
                   w.end_date    AS web_special_end_date,
                   a.id          AS amenity_id,
                   a.name        AS amenity_name,
                   a.featured    AS amenity_featured,
                   a.type        AS amenity_type
            FROM floorplan f
                     LEFT JOIN unit u ON f.id = u.floorplan_id AND u.active = true
                     LEFT JOIN web_special w ON f.id = w.floorplan_id AND w.start_date <= CURRENT_DATE
                AND (w.end_date IS NULL OR w.end_date >= CURRENT_DATE)
                     LEFT JOIN amenity a ON f.id = a.floorplan_id
            """;

    public List<FloorplanDetails> getFloorplanDetailsForProperty(String propertyId) {

        SqlBuilder sqlBuilder = new SqlBuilder(FLOORPLAN_DETAILS_SQL)
                .where("f.property_id = :propertyId", "propertyId", propertyId)
                .where("f.style != :style", "style", "GARAGE")
                .where("f.active = :floorplanActive", "floorplanActive", true);

        return jdbcTemplate.query(sqlBuilder.sql(), sqlBuilder.params(), floorplanDetailsExtractor);
    }

    public List<FloorplanDetails> getActiveFloorplansDetails() {

        SqlBuilder sqlBuilder = new SqlBuilder(FLOORPLAN_DETAILS_SQL)
                .where("f.style != :style", "style", "GARAGE")
                .where("f.active = :floorplanActive", "floorplanActive", true);

        return jdbcTemplate.query(sqlBuilder.sql(), sqlBuilder.params(), floorplanDetailsExtractor);
    }

    public Optional<FloorplanDetails> getFloorplanDetails(String floorplanId) {

        SqlBuilder sqlBuilder = new SqlBuilder(FLOORPLAN_DETAILS_SQL)
                .where("f.id = :floorplanId", "floorplanId", floorplanId)
                .where("f.style != :style", "style", "GARAGE");

        var floorplanDetails = jdbcTemplate.query(sqlBuilder.sql(), sqlBuilder.params(), floorplanDetailsExtractor);
        return floorplanDetails == null || floorplanDetails.isEmpty()
                ? Optional.empty()
                : Optional.ofNullable(floorplanDetails.getFirst());
    }

    private static final String SPOTLIGHT_SQL =
            """
            SELECT f.id,
                   f.name,
                   f.meta_description,
                   f.bedroom,
                   f.bathroom,
                   f.style,
                   f.cover_image,
                   p.id   as property_id,
                   p.name as property_name,
                   p.address as property_address,
                   p.zipcode as property_zipcode,
                   u.id   AS unit_id,
                   u.square_foot,
                   u.rent,
                   u.address as unit_address,
                   u.zipcode as unit_zipcode
            FROM floorplan f
                     JOIN property p on f.property_id = p.id AND p.active = true
                     LEFT JOIN unit u ON f.id = u.floorplan_id AND u.active = true

            """;

    public List<FloorplanSpotlight> getFeaturedSpotlights() {
        SqlBuilder sqlBuilder = new SqlBuilder(SPOTLIGHT_SQL)
                .where("f.active = :active", "active", true)
                .where("f.featured = :featured", "featured", true);
        return jdbcTemplate.query(sqlBuilder.sql(), sqlBuilder.params(), floorplanSpotlightExtractor);
    }

    public Optional<FloorplanSpotlight> getFloorplanSpotlight(String floorplanId) {
        SqlBuilder sqlBuilder = new SqlBuilder(SPOTLIGHT_SQL).where("f.id = :floorplanId", "floorplanId", floorplanId);
        var spotlights = jdbcTemplate.query(sqlBuilder.sql(), sqlBuilder.params(), floorplanSpotlightExtractor);
        return spotlights == null || spotlights.isEmpty()
                ? Optional.empty()
                : Optional.ofNullable(spotlights.getFirst());
    }
}
