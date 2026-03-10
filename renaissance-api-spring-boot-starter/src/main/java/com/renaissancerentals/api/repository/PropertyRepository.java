package com.renaissancerentals.api.repository;

import com.renaissancerentals.api.domain.Amenity;
import com.renaissancerentals.api.domain.PropertyBusRoute;
import com.renaissancerentals.api.domain.TeamMember;
import com.renaissancerentals.api.domain.projection.PropertyDetails;
import com.renaissancerentals.api.domain.projection.PropertyListing;
import com.renaissancerentals.api.domain.projection.PropertySummary;
import com.renaissancerentals.api.domain.template.PropertyAmenityMapper;
import com.renaissancerentals.api.domain.template.PropertyBusRouteMapper;
import com.renaissancerentals.api.repository.helper.SqlBuilder;
import com.renaissancerentals.api.repository.mapper.PropertyDetailsJdbcMapper;
import com.renaissancerentals.api.repository.mapper.PropertyListingExtractor;
import com.renaissancerentals.api.repository.mapper.PropertySummaryExtractor;
import com.renaissancerentals.api.repository.mapper.TeamMemberJdbcMapper;
import com.renaissancerentals.persistence.dao.PropertyAmenityDao;
import com.renaissancerentals.persistence.dao.PropertyBusRouteDao;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PropertyRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final TeamMemberJdbcMapper teamMemberDetailsMapper;
    private final PropertyDetailsJdbcMapper propertyDetailsJdbcMapper;

    private final FloorplanRepository floorplanRepository;
    private final PropertyBusRouteDao propertyBusRouteDao;
    private final PropertyAmenityDao propertyAmenityDao;
    private final PropertyAmenityMapper propertyAmenityMapper;
    private final PropertyBusRouteMapper propertyBusRouteMapper;

    private final PropertyListingExtractor propertyListingExtractor;

    private static final String TEAM_MEMBER_SQL = """
            SELECT DISTINCT
                tm.*
            FROM team_member tm
                     JOIN team_member_property tmp
                          ON tmp.team_member_id = tm.id
            """;
    private final PropertySummaryExtractor propertySummaryExtractor;

    public Optional<TeamMember> getPropertyManager(String propertyId) {
        SqlBuilder sqlBuilder = new SqlBuilder(TEAM_MEMBER_SQL)
                .where("tm.job_title = :jobTitle", "jobTitle", "Brand Manager")
                .where("tmp.property_id = :propertyId", "propertyId", propertyId);
        return jdbcTemplate.query(sqlBuilder.sql(), sqlBuilder.params(), teamMemberDetailsMapper).stream().findFirst();

    }

    public List<TeamMember> getPropertyTeamMembers(String propertyId) {
        SqlBuilder sqlBuilder = new SqlBuilder(TEAM_MEMBER_SQL).where("tmp.property_id = :propertyId", "propertyId",
                propertyId);
        return jdbcTemplate.query(sqlBuilder.sql(), sqlBuilder.params(), teamMemberDetailsMapper);
    }

    public Optional<PropertyDetails> getProperty(String propertyId) {
        SqlBuilder sqlBuilder = new SqlBuilder("""
                SELECT p.*,
                       l.id                       as leasingOfficeId,
                       l.name                     as leasingOfficeName,
                       l.address                  as leasingOfficeAddress,
                       l.zipcode                  as leasingOfficeZipcode,
                       l.phone                    as leasingOfficePhone,
                       l.office_hours             as leasingOfficeHours,
                       l.direction                as leasingOfficeDirection,
                       l.office_map               as leasingOfficeMap,
                       l.office_map_landscape     as leasingOfficeMapLandscape,
                       l.office_image             as leasingOfficeImage,
                       l.office_image_description as leasingOfficeImageDescription
                from property p
                         join leasing_office l on p.leasing_office_id = l.id
                """).where("p.id = :id", "id", propertyId);
        return jdbcTemplate.query(sqlBuilder.sql(), sqlBuilder.params(), propertyDetailsJdbcMapper).stream().findFirst()
                .map(propertyDetails -> {
                    propertyDetails.setAmenities(getPropertyAmenities(propertyId));
                    propertyDetails.setBusRoutes(getPropertyBusRoutes(propertyId));
                    propertyDetails.setTeamMembers(getPropertyTeamMembers(propertyId));
                    propertyDetails.setFloorplans(floorplanRepository.getFloorplanDetailsForProperty(propertyId));
                    return propertyDetails;
                });
    }

    private static final String PROPERTY_LISTING_SQL = """
            SELECT p.id          AS propertyId,
                   p.name        AS propertyName,
                   p.lease_type  AS propertyLeaseType,
                   f.id          AS floorplanId,
                   f.name        AS floorplanName,
                   f.bedroom,
                   f.bathroom,
                   f.cover_image,
                   f.featured,
                   f.style,
                   f.special_rent,
                   f.special_rent_start_date,
                   f.special_rent_end_date,
                   f.address,
                   f.zipcode,
                   f.video_tour_link,
                   f.virtual_tour_link,
                   f.photos_folder_id,
                   u.id          AS unitId,
                   u.rent        AS unitRent,
                   u.square_foot AS unitSquareFoot,
                   u.move_in_date,
                   u.availability_extension_months,
                   w.description AS webSpecialDescription
            FROM property p
                     INNER JOIN floorplan f ON f.property_id = p.id
                     INNER JOIN unit u ON u.floorplan_id = f.id
                     LEFT JOIN web_special w
                            ON w.floorplan_id = f.id
                           AND w.start_date <= CURRENT_DATE
                           AND (w.end_date IS NULL OR w.end_date >= CURRENT_DATE)
            """;

    public Optional<PropertyListing> getPropertyListing(String propertyId) {

        SqlBuilder sqlBuilder = new SqlBuilder(PROPERTY_LISTING_SQL).where("p.id = :propertyId", "propertyId", propertyId)
                .where("f.style != :style", "style", "GARAGE").where("f.active = :floorplanActive", "floorplanActive", true)
                .where("u.active = :unitActive", "unitActive", true);

        var propertyListing = jdbcTemplate.query(sqlBuilder.sql(), sqlBuilder.params(), propertyListingExtractor);

        return propertyListing == null || propertyListing.isEmpty()
                ? Optional.empty()
                : Optional.ofNullable(propertyListing.getFirst());
    }

    public List<PropertyListing> getPropertyListings() {

        SqlBuilder sqlBuilder = new SqlBuilder(PROPERTY_LISTING_SQL)
                .where("f.style != :style", "style", "GARAGE")
                .where("p.lease_type = :leaseType", "leaseType", "YEARLY")
                .where("p.active = :propertyActive", "propertyActive", true)
                .where("f.active = :floorplanActive", "floorplanActive", true)
                .where("u.active = :unitActive", "unitActive", true);

        return jdbcTemplate.query(sqlBuilder.sql(), sqlBuilder.params(), propertyListingExtractor);
    }


    public Optional<PropertySummary> getPropertySummaryForFloorplan(String floorplanId) {
        SqlBuilder sqlBuilder = new SqlBuilder(
                """
                        SELECT p.id,
                               p.name,
                               p.address,
                               p.zipcode,
                               p.email,
                               p.phone,
                               p.lease_type,
                               pb.id AS bus_route_id,
                               pb.bus_route,
                               pb.bus_route_link
                        FROM property p
                                 LEFT JOIN property_bus_route pb
                                           ON pb.property_id = p.id
                                INNER JOIN floorplan f on p.id = f.property_id
                        """
        ).where("f.id = :floorplanId", "floorplanId", floorplanId);
        var propertySummary = jdbcTemplate.query(sqlBuilder.sql(), sqlBuilder.params(), propertySummaryExtractor);
        return propertySummary == null || propertySummary.isEmpty()
                ? Optional.empty()
                : Optional.of(propertySummary.getFirst());
    }
    public Optional<PropertySummary> getPropertySummaryForProperty(String propertyId) {
        SqlBuilder sqlBuilder = new SqlBuilder(
                """
                        SELECT p.id,
                               p.name,
                               p.address,
                               p.zipcode,
                               p.email,
                               p.phone,
                               p.lease_type,
                               pb.id AS bus_route_id,
                               pb.bus_route,
                               pb.bus_route_link
                        FROM property p
                                 LEFT JOIN property_bus_route pb
                                           ON pb.property_id = p.id
                        """
        ).where("p.id = :propertyId", "propertyId", propertyId);
        var propertySummary = jdbcTemplate.query(sqlBuilder.sql(), sqlBuilder.params(), propertySummaryExtractor);
        return propertySummary == null || propertySummary.isEmpty()
                ? Optional.empty()
                : Optional.of(propertySummary.getFirst());
    }

    private List<Amenity> getPropertyAmenities(String propertyId) {
        return propertyAmenityDao.findByPropertyId(propertyId).stream().map(propertyAmenityMapper::toDomain).toList();
    }

    private List<PropertyBusRoute> getPropertyBusRoutes(String propertyId) {
        return propertyBusRouteDao.findByPropertyId(propertyId).stream().map(propertyBusRouteMapper::toDomain).toList();
    }
}
