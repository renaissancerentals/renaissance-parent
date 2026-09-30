package com.renaissancerentals.foundation.seo;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** schema.org JSON-LD builders. Only facts that exist in the data are emitted. */
public class SeoJsonLd {

    private static final String CONTEXT = "https://schema.org";

    private final SeoProperties properties;
    private final SiteLinks links;

    public SeoJsonLd(SeoProperties properties, SiteLinks links) {
        this.properties = properties;
        this.links = links;
    }

    private static Map<String, Object> node(String type) {
        var map = new LinkedHashMap<String, Object>();
        map.put("@context", CONTEXT);
        map.put("@type", type);
        return map;
    }

    private static void put(Map<String, Object> map, String key, Object value) {
        if (value == null || (value instanceof String s && s.isBlank())) {
            return;
        }
        if (value instanceof List<?> l && l.isEmpty()) {
            return;
        }
        map.put(key, value);
    }

    private Map<String, Object> address(String street, String zipcode) {
        var org = properties.organization();
        var address = new LinkedHashMap<String, Object>();
        address.put("@type", "PostalAddress");
        put(address, "streetAddress", street == null ? null : stripCity(street));
        put(address, "addressLocality", org.locality());
        put(address, "addressRegion", org.region());
        put(address, "postalCode", zipcode);
        put(address, "addressCountry", org.country());
        return address;
    }

    /** Some addresses are stored as "102 E 17th St, Bloomington, IN"; the city is emitted separately. */
    private String stripCity(String street) {
        var org = properties.organization();
        return street.replaceAll(
                        "(?i),\\s*" + java.util.regex.Pattern.quote(org.locality()) + "\\s*,?\\s*("
                                + java.util.regex.Pattern.quote(org.region()) + "|Indiana)?\\s*(\\d{5})?\\s*$",
                        "")
                .trim();
    }

    public Map<String, Object> organization() {
        var org = properties.organization();
        var map = node("Organization");
        put(map, "@id", links.hubOrigin() + "/#organization");
        put(map, "name", org.name() != null ? org.name() : properties.siteName());
        put(map, "url", links.hubOrigin() + "/");
        put(map, "logo", links.absolute(org.logo()));
        put(map, "telephone", SeoText.phone(org.telephone()));
        put(map, "email", org.email());
        if (org.streetAddress() != null || org.postalCode() != null) {
            map.put("address", address(org.streetAddress(), org.postalCode()));
        }
        put(map, "sameAs", org.sameAs());
        return map;
    }

    public Map<String, Object> webSite() {
        var map = node("WebSite");
        put(map, "name", properties.siteName());
        put(map, "url", links.self("/"));
        return map;
    }

    public Map<String, Object> apartmentComplex(
            SeoData.Property property, String url, String description, String image) {
        var map = node("ApartmentComplex");
        put(map, "@id", url + "#apartments");
        put(map, "name", property.name());
        put(map, "url", url);
        put(map, "description", description);
        put(map, "image", image);
        put(map, "telephone", SeoText.phone(property.phone()));
        put(map, "email", property.email());
        map.put("address", address(property.address(), property.zipcode()));
        var features = new ArrayList<Map<String, Object>>();
        if (property.amenities() != null) {
            for (var amenity : property.amenities()) {
                if (!SeoText.blank(amenity.name())) {
                    var feature = new LinkedHashMap<String, Object>();
                    feature.put("@type", "LocationFeatureSpecification");
                    feature.put("name", amenity.name());
                    feature.put("value", true);
                    features.add(feature);
                }
            }
        }
        put(map, "amenityFeature", features);
        var sameAs = new ArrayList<String>();
        if (!SeoText.blank(property.facebookLink())) sameAs.add(property.facebookLink());
        if (!SeoText.blank(property.twitterLink())) sameAs.add(property.twitterLink());
        put(map, "sameAs", sameAs);
        var parent = new LinkedHashMap<String, Object>();
        parent.put("@type", "Organization");
        parent.put("@id", links.hubOrigin() + "/#organization");
        parent.put(
                "name",
                properties.organization().name() != null
                        ? properties.organization().name()
                        : properties.siteName());
        parent.put("url", links.hubOrigin() + "/");
        map.put("parentOrganization", parent);
        return map;
    }

    public Map<String, Object> floorPlan(SeoData.Floorplan floorplan, String url, String description, String image) {
        var map = node("FloorPlan");
        put(map, "@id", url + "#floorplan");
        put(map, "name", floorplan.name());
        put(map, "url", url);
        put(map, "description", description);
        put(map, "image", image);
        put(map, "numberOfBedrooms", floorplan.bedroom());
        put(map, "numberOfBathroomsTotal", floorplan.bathroom());
        return map;
    }

    /** One offer per available unit, with the lowest advertised monthly rent. */
    public List<Map<String, Object>> offers(SeoData.Floorplan floorplan, List<SeoData.Unit> units, String url) {
        var offers = new ArrayList<Map<String, Object>>();
        if (units == null) {
            return offers;
        }
        for (var unit : units) {
            var price = lowestRent(unit);
            if (price == null) {
                continue;
            }
            var offer = node("Offer");
            put(offer, "url", url);
            offer.put("priceCurrency", "USD");
            offer.put("price", price);
            offer.put("priceSpecification", monthly(price));
            put(
                    offer,
                    "availabilityStarts",
                    unit.moveInDate() == null ? null : unit.moveInDate().toString());
            var apartment = new LinkedHashMap<String, Object>();
            apartment.put("@type", "Apartment");
            put(apartment, "name", floorplan.name());
            put(apartment, "numberOfBedrooms", floorplan.bedroom());
            put(apartment, "numberOfBathroomsTotal", floorplan.bathroom());
            if (unit.squareFoot() != null && unit.squareFoot() > 0) {
                var size = new LinkedHashMap<String, Object>();
                size.put("@type", "QuantitativeValue");
                size.put("value", unit.squareFoot());
                size.put("unitCode", "FTK");
                apartment.put("floorSize", size);
            }
            var pets = SeoFacts.pets(!SeoText.blank(unit.allowedPet()) ? unit.allowedPet() : floorplan.allowedPet());
            if (pets != null) {
                apartment.put("petsAllowed", !pets.startsWith("No pets"));
            }
            var included = SeoFacts.includedUtilities(floorplan.utilities());
            if (!included.isEmpty()) {
                var features = new ArrayList<Map<String, Object>>();
                for (var name : included) {
                    var feature = new LinkedHashMap<String, Object>();
                    feature.put("@type", "LocationFeatureSpecification");
                    feature.put("name", name + " included");
                    feature.put("value", true);
                    features.add(feature);
                }
                apartment.put("amenityFeature", features);
            }
            var street = unit.address() != null ? unit.address() : floorplan.address();
            var zip = unit.zipcode() != null ? unit.zipcode() : floorplan.zipcode();
            if (street != null || zip != null) {
                apartment.put("address", address(street, zip));
            }
            offer.put("itemOffered", apartment);
            offers.add(offer);
        }
        return offers;
    }

    private static Map<String, Object> monthly(Number price) {
        var spec = new LinkedHashMap<String, Object>();
        spec.put("@type", "UnitPriceSpecification");
        spec.put("price", price);
        spec.put("priceCurrency", "USD");
        spec.put("unitCode", "MON");
        return spec;
    }

    static Float lowestRent(SeoData.Unit unit) {
        Float best = null;
        for (var candidate : new Float[] {unit.rent(), unit.discountedRent()}) {
            if (candidate != null && candidate > 0 && (best == null || candidate < best)) {
                best = candidate;
            }
        }
        return best;
    }

    public Map<String, Object> faqPage(List<SeoData.Faq> faqs) {
        var questions = new ArrayList<Map<String, Object>>();
        for (var faq : faqs) {
            var question = SeoText.plain(faq.question());
            var answer = SeoText.plain(faq.answer());
            if (question.isEmpty() || answer.isEmpty()) {
                continue;
            }
            var q = new LinkedHashMap<String, Object>();
            q.put("@type", "Question");
            q.put("name", question);
            var a = new LinkedHashMap<String, Object>();
            a.put("@type", "Answer");
            a.put("text", answer);
            q.put("acceptedAnswer", a);
            questions.add(q);
        }
        if (questions.isEmpty()) {
            return null;
        }
        var map = node("FAQPage");
        map.put("mainEntity", questions);
        return map;
    }

    /** Pairs of (name, absolute url), first is the top of the trail. */
    public Map<String, Object> breadcrumbs(List<String[]> trail) {
        var items = new ArrayList<Map<String, Object>>();
        var position = 1;
        for (var step : trail) {
            var item = new LinkedHashMap<String, Object>();
            item.put("@type", "ListItem");
            item.put("position", position++);
            item.put("name", step[0]);
            item.put("item", step[1]);
            items.add(item);
        }
        var map = node("BreadcrumbList");
        map.put("itemListElement", items);
        return map;
    }

    public Map<String, Object> jobPosting(SeoData.Job job, String url) {
        var map = node("JobPosting");
        put(map, "title", job.title());
        put(map, "description", job.description());
        put(map, "url", url);
        put(
                map,
                "datePosted",
                job.datePosted() == null ? null : job.datePosted().toString());
        put(
                map,
                "validThrough",
                job.validThrough() == null ? null : job.validThrough().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        put(map, "employmentType", job.employmentType());
        var hirer = new LinkedHashMap<String, Object>();
        hirer.put("@type", "Organization");
        hirer.put(
                "name",
                properties.organization().name() != null
                        ? properties.organization().name()
                        : properties.siteName());
        hirer.put("sameAs", links.hubOrigin() + "/");
        map.put("hiringOrganization", hirer);
        var place = new LinkedHashMap<String, Object>();
        place.put("@type", "Place");
        place.put(
                "address",
                address(
                        properties.organization().streetAddress(),
                        properties.organization().postalCode()));
        map.put("jobLocation", place);
        if (job.salary() != null && job.salary() > 0 && job.salaryType() != null) {
            var value = new LinkedHashMap<String, Object>();
            value.put("@type", "QuantitativeValue");
            value.put("value", job.salary());
            value.put("unitText", job.salaryType());
            var pay = new LinkedHashMap<String, Object>();
            pay.put("@type", "MonetaryAmount");
            pay.put("currency", "USD");
            pay.put("value", value);
            map.put("baseSalary", pay);
        }
        return map;
    }
}
