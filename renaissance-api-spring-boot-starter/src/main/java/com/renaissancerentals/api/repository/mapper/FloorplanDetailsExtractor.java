package com.renaissancerentals.api.repository.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.renaissancerentals.api.domain.Amenity;
import com.renaissancerentals.api.domain.WebSpecial;
import com.renaissancerentals.api.domain.projection.FloorplanDetails;
import com.renaissancerentals.api.domain.projection.UnitDetails;

@Component
public class FloorplanDetailsExtractor implements ResultSetExtractor<List<FloorplanDetails>> {

    @Override
    public List<FloorplanDetails> extractData(ResultSet rs) throws SQLException, DataAccessException{
        Map<String, FloorplanDetails> floorplanMap = new LinkedHashMap<>();

        while (rs.next()) {
            String floorplanId = rs.getString("id");

            // --- Floorplan fields ---
            String name = rs.getString("name");
            int bedroom = rs.getInt("bedroom");
            float bathroom = rs.getFloat("bathroom");
            String style = rs.getString("style");
            float specialRent = rs.getFloat("special_rent");
            LocalDate specialRentStartDate = rs.getDate("special_rent_start_date") != null
                    ? rs.getDate("special_rent_start_date").toLocalDate()
                    : null;
            LocalDate specialRentEndDate = rs.getDate("special_rent_end_date") != null
                    ? rs.getDate("special_rent_end_date").toLocalDate()
                    : null;
            String address = rs.getString("address");
            String zipcode = rs.getString("zipcode");
            boolean featured = rs.getBoolean("featured");
            boolean greenCertified = rs.getBoolean("green_certified");
            String videoTourLink = rs.getString("video_tour_link");
            String threeSixtyVideoTourLink = rs.getString("three_sixty_video_tour_link");
            String virtualTourLink = rs.getString("virtual_tour_link");
            String photo = rs.getString("photo");
            String coverImage = rs.getString("cover_image");
            String floorPlanFolderId = rs.getString("floor_plan_folder_id");
            String photosFolderId = rs.getString("photos_folder_id");

            // --- Unit fields ---
            String unitId = rs.getString("unit_id");
            int squareFoot = rs.getInt("square_foot");
            String allowedPet = rs.getString("allowed_pet");
            float rent = rs.getFloat("rent");
            float discountedRent = rs.getFloat("discounted_rent");
            float deposit = rs.getFloat("deposit");
            boolean furnished = rs.getBoolean("furnished");
            int garages = rs.getInt("garages");
            LocalDate moveInDate = rs.getDate("move_in_date") != null ? rs.getDate("move_in_date").toLocalDate() : null;
            int availabilityExtensionMonths = rs.getInt("availability_extension_months");

            // --- WebSpecial fields ---
            long webSpecialId = rs.getLong("web_special_id");
            String webSpecialDescription = rs.getString("web_special_description");
            LocalDate webSpecialStartDate = rs.getDate("web_special_start_date") != null
                    ? rs.getDate("web_special_start_date").toLocalDate()
                    : null;
            LocalDate webSpecialEndDate = rs.getDate("web_special_end_date") != null
                    ? rs.getDate("web_special_end_date").toLocalDate()
                    : null;

            // --- Amenity fields ---
            long amenityId = rs.getLong("amenity_id");
            String amenityName = rs.getString("amenity_name");
            boolean amenityFeatured = rs.getBoolean("amenity_featured");
            String amenityType = rs.getString("amenity_type");

            // --- Floorplan: create if not exists ---
            FloorplanDetails floorplanDetails = floorplanMap.computeIfAbsent(floorplanId,
                    id -> FloorplanDetails.builder().id(floorplanId).name(name).bedroom(bedroom).bathroom(bathroom)
                            .style(style).specialRent(specialRent).specialRentStartDate(specialRentStartDate)
                            .specialRentEndDate(specialRentEndDate).address(address).zipcode(zipcode).featured(featured)
                            .greenCertified(greenCertified).videoTourLink(videoTourLink)
                            .threeSixtyVideoTourLink(threeSixtyVideoTourLink).virtualTourLink(virtualTourLink)
                            .photo(photo).coverImage(coverImage).floorPlanFolderId(floorPlanFolderId)
                            .photosFolderId(photosFolderId).units(new ArrayList<>()).webSpecials(new ArrayList<>())
                            .amenities(new ArrayList<>()).build());

            // --- Add Unit ---
            if (unitId != null && floorplanDetails.getUnits().stream().noneMatch(u -> u.getId().equals(unitId))) {
                floorplanDetails.getUnits()
                        .add(UnitDetails.builder().id(unitId).squareFoot(squareFoot).allowedPet(allowedPet).rent(rent)
                                .discountedRent(discountedRent).deposit(deposit).furnished(furnished).garages(garages)
                                .moveInDate(moveInDate).availabilityExtensionMonths(availabilityExtensionMonths)
                                .build());
            }

            // --- Add WebSpecial ---
            if (webSpecialId != 0
                    && floorplanDetails.getWebSpecials().stream().noneMatch(w -> w.getId() == webSpecialId)) {
                floorplanDetails.getWebSpecials()
                        .add(WebSpecial.builder().id(webSpecialId).description(webSpecialDescription)
                                .startDate(webSpecialStartDate).endDate(webSpecialEndDate).build());
            }

            // --- Add Amenity ---
            if (amenityId != 0 && floorplanDetails.getAmenities().stream().noneMatch(a -> a.getId() == amenityId)) {
                floorplanDetails.getAmenities().add(Amenity.builder().id(amenityId).name(amenityName).type(amenityType)
                        .featured(amenityFeatured).build());
            }
        }

        return new ArrayList<>(floorplanMap.values());
    }
}
