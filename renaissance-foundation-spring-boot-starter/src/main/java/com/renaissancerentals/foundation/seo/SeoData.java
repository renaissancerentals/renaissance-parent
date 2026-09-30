package com.renaissancerentals.foundation.seo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/** The slice of listing data that SEO needs. Identical JSON shape to the public API, unknown fields ignored. */
public final class SeoData {

    private SeoData() {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Named(String name, Boolean featured, String type) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record BusRoute(String busRoute, String busRouteLink) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record LeasingOffice(
            String name, String address, String zipcode, String phone, String officeHours, String direction) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Utility(String name, String type, Float averageMonthlyBill) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record PropertyRef(String id, String name, String address, String zipcode, String email, String phone) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Property(
            String id,
            String name,
            String address,
            String zipcode,
            String email,
            String phone,
            String facebookLink,
            String twitterLink,
            String coverImage,
            String description,
            String htmlTitle,
            String metaDescription,
            List<Named> amenities,
            List<Floorplan> floorplans,
            String leaseType,
            List<BusRoute> busRoutes,
            LeasingOffice leasingOffice) {
        public Property {
            amenities = amenities == null ? List.of() : List.copyOf(amenities);
            floorplans = floorplans == null ? List.of() : List.copyOf(floorplans);
            busRoutes = busRoutes == null ? List.of() : List.copyOf(busRoutes);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Floorplan(
            String id,
            String name,
            Integer bedroom,
            Float bathroom,
            String style,
            String address,
            String zipcode,
            String description,
            String htmlTitle,
            String metaDescription,
            String coverImage,
            PropertyRef property,
            List<Unit> units,
            List<Named> amenities,
            String allowedPet,
            String petPolicy,
            String highlights,
            List<Utility> utilities) {
        public Floorplan {
            units = units == null ? List.of() : List.copyOf(units);
            amenities = amenities == null ? List.of() : List.copyOf(amenities);
            utilities = utilities == null ? List.of() : List.copyOf(utilities);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Unit(
            String id,
            Integer squareFoot,
            Float rent,
            Float discountedRent,
            Float deposit,
            LocalDate moveInDate,
            String address,
            String zipcode,
            Boolean furnished,
            String coverImage,
            Floorplan floorplan,
            String level,
            String features,
            String allowedPet) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Listing(String id, String name, List<ListingFloorplan> floorplans) {
        public Listing {
            floorplans = floorplans == null ? List.of() : List.copyOf(floorplans);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ListingFloorplan(
            String id,
            String name,
            Integer bedroom,
            Float bathroom,
            String address,
            String zipcode,
            List<ListingUnit> units) {
        public ListingFloorplan {
            units = units == null ? List.of() : List.copyOf(units);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ListingUnit(String id, Float rent, Integer squareFoot, LocalDate moveInDate) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Faq(String question, String answer) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Sublet(
            String assetKey,
            String title,
            String description,
            Integer bedroom,
            Float rent,
            String address,
            String zipcode,
            String coverImage,
            LocalDate availableFrom,
            LocalDate availableTo,
            LocalDateTime createdDate) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Job(
            Long id,
            String title,
            String description,
            LocalDateTime validThrough,
            String employmentType,
            Float salary,
            String salaryType,
            LocalDate datePosted) {}
}
