package com.renaissancerentals.api.repository.mapper;

import com.renaissancerentals.api.domain.PropertyBusRoute;
import com.renaissancerentals.api.domain.projection.PropertySummary;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class PropertySummaryExtractor implements ResultSetExtractor<List<PropertySummary>> {

    @Override
    public List<PropertySummary> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String, PropertySummary> propertyMap = new LinkedHashMap<>();

        while (rs.next()) {
            String id = rs.getString("id");
            String name = rs.getString("name");
            String address = rs.getString("address");
            String zipcode = rs.getString("zipcode");
            String email = rs.getString("email");
            String phone = rs.getString("phone");
            String leaseType = rs.getString("lease_type");
            String busRouteId = rs.getString("bus_route_id");
            String busRoute = rs.getString("bus_route");
            String busRouteLink = rs.getString("bus_route_link");

            propertyMap.computeIfAbsent(id, propertyId ->
                    PropertySummary.builder()
                            .id(id).name(name).address(address)
                            .zipcode(zipcode).email(email).phone(phone)
                            .leaseType(leaseType).busRoutes(new ArrayList<>()).build());

            if (busRouteId != null) {
                propertyMap.get(id).getBusRoutes().add(PropertyBusRoute.builder().id(busRouteId).busRoute(busRoute).busRouteLink(busRouteLink).build());
            }

        }

        return new ArrayList<>(propertyMap.values());
    }
}
