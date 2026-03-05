package com.renaissancerentals.api.repository.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import com.renaissancerentals.api.domain.projection.FloorplanSpotlight;
import com.renaissancerentals.api.domain.projection.PropertySpotlight;
import com.renaissancerentals.api.domain.projection.UnitSpotlight;

@Component
public class FloorplanSpotlightExtractor implements ResultSetExtractor<List<FloorplanSpotlight>> {

    @Override
    public List<FloorplanSpotlight> extractData(ResultSet rs) throws SQLException, DataAccessException{
        Map<String, FloorplanSpotlight> floorplanSpotlightMap = new LinkedHashMap<>();

        while (rs.next()) {
            String floorplanId = rs.getString("id");
            String name = rs.getString("name");
            String metaDescription = rs.getString("meta_description");
            Integer bedroom = rs.getInt("bedroom");
            Float bathroom = rs.getFloat("bathroom");
            String style = rs.getString("style");
            String coverImage = rs.getString("cover_image");
            String propertyId = rs.getString("property_id");
            String propertyName = rs.getString("property_name");
            String propertyAddress = rs.getString("property_address");
            String propertyZipcode = rs.getString("property_zipcode");
            String unitId = rs.getString("unit_id");
            Integer squareFoot = rs.getInt("square_foot");
            Float rent = rs.getFloat("rent");
            String unitAddress = rs.getString("unit_address");
            String unitZipcode = rs.getString("unit_zipcode");

            floorplanSpotlightMap.computeIfAbsent(floorplanId,
                    id -> FloorplanSpotlight.builder().id(floorplanId).name(name).metaDescription(metaDescription)
                            .bedroom(bedroom).bathroom(bathroom).style(style).coverImage(coverImage)
                            .units(new ArrayList<>()).property(PropertySpotlight.builder().id(propertyId)
                                    .name(propertyName).address(propertyAddress).zipcode(propertyZipcode).build())
                            .build());

            if (unitId != null) {
                floorplanSpotlightMap.get(floorplanId).getUnits().add(UnitSpotlight.builder().id(unitId)
                        .squareFoot(squareFoot).rent(rent).address(unitAddress).zipcode(unitZipcode).build());
            }
        }

        return new ArrayList<>(floorplanSpotlightMap.values());

    }
}
