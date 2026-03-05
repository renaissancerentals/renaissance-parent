package com.renaissancerentals.api.repository.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.renaissancerentals.api.domain.projection.FloorplanListing;
import com.renaissancerentals.api.domain.projection.PropertyListing;
import com.renaissancerentals.api.domain.projection.UnitListing;

@Component
public class PropertyListingExtractor implements ResultSetExtractor<List<PropertyListing>> {

    @Override
    public List<PropertyListing> extractData(ResultSet rs) throws SQLException, DataAccessException{
        Map<String, PropertyListing> propertyMap = new LinkedHashMap<>();

        while (rs.next()) {
            String propertyId = rs.getString("propertyId");
            String propertyName = rs.getString("propertyName");

            String floorplanId = rs.getString("floorplanId");
            if (floorplanId == null)
                continue; // skip if no floorplan

            String floorplanName = rs.getString("floorplanName");
            Integer bedroom = rs.getObject("bedroom",Integer.class);
            Float bathroom = rs.getObject("bathroom",Float.class);
            String coverImage = rs.getString("cover_image");
            Boolean featured = rs.getObject("featured",Boolean.class);
            String style = rs.getString("style");
            Float specialRent = rs.getObject("special_rent",Float.class);
            LocalDate specialRentStartDate = rs.getObject("special_rent_start_date",LocalDate.class);
            LocalDate specialRentEndDate = rs.getObject("special_rent_end_date",LocalDate.class);
            String address = rs.getString("address");
            String zipcode = rs.getString("zipcode");
            String videoTourLink = rs.getString("video_tour_link");
            String virtualTourLink = rs.getString("virtual_tour_link");
            String photosFolderId = rs.getString("photos_folder_id");

            String unitId = rs.getString("unitId");
            Float unitRent = rs.getObject("unitRent",Float.class);
            Integer unitSquareFoot = rs.getObject("unitSquareFoot",Integer.class);
            LocalDate moveInDate = rs.getObject("move_in_date",LocalDate.class);
            Integer availabilityMonths = rs.getObject("availability_extension_months",Integer.class);

            String webSpecialDescription = rs.getString("webSpecialDescription");

            // --- Property ---
            PropertyListing property = propertyMap.computeIfAbsent(propertyId,id -> PropertyListing.builder()
                    .id(propertyId).name(propertyName).floorplans(new ArrayList<>()).build());

            // --- Floorplan map for deduplication ---
            Map<String, FloorplanListing> floorplanMap = property.getFloorplans().stream()
                    .collect(Collectors.toMap(FloorplanListing::getId,f -> f));

            FloorplanListing floorplan = floorplanMap.get(floorplanId);
            if (floorplan == null) {
                floorplan = FloorplanListing.builder().id(floorplanId).name(floorplanName).bedroom(bedroom)
                        .bathroom(bathroom).coverImage(coverImage).featured(featured).style(style)
                        .specialRent(specialRent).specialRentStartDate(specialRentStartDate)
                        .specialRentEndDate(specialRentEndDate).address(address).zipcode(zipcode)
                        .videoTourLink(videoTourLink).virtualTourLink(virtualTourLink).photosFolderId(photosFolderId)
                        .units(new ArrayList<>()).webSpecials(new ArrayList<>()).build();

                property.getFloorplans().add(floorplan);
                floorplanMap.put(floorplanId,floorplan);
            }

            // --- Add web special ---
            if (webSpecialDescription != null && !floorplan.getWebSpecials().contains(webSpecialDescription)) {
                floorplan.getWebSpecials().add(webSpecialDescription);
            }

            // --- Add unit ---
            if (unitId != null) {
                boolean unitExists = floorplan.getUnits().stream().anyMatch(u -> u.getId().equals(unitId));
                if (!unitExists) {
                    floorplan.getUnits().add(UnitListing.builder().id(unitId).rent(unitRent).squareFoot(unitSquareFoot)
                            .moveInDate(moveInDate).availabilityExtensionMonths(availabilityMonths).build());
                }
            }
        }

        return new ArrayList<>(propertyMap.values());
    }
}
