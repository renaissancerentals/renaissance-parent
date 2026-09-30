package com.renaissancerentals.foundation.seo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Clock;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

/** Reads the public API of the data hosts, trying them in order. Answers are cached for a few minutes. */
public class RemoteSeoDataSource implements SeoDataSource {

    private static final Logger log = LoggerFactory.getLogger(RemoteSeoDataSource.class);

    private final RestClient client;
    private final List<String> baseUrls;
    private final ObjectMapper mapper;
    private final TtlCache<String, Optional<String>> cache;

    public RemoteSeoDataSource(RestClient client, List<String> baseUrls, ObjectMapper mapper, Clock clock) {
        this.client = client;
        this.baseUrls = baseUrls;
        this.mapper = mapper;
        this.cache = new TtlCache<>(Duration.ofMinutes(10), 2000, clock);
    }

    @Override
    public Optional<SeoData.Property> property(String propertyId) {
        return read("/api/properties/" + propertyId + "?projection=DETAILS", SeoData.Property.class);
    }

    @Override
    public List<SeoData.Listing> listings() {
        return readList("/api/properties?projection=FILTER", new TypeReference<>() {});
    }

    @Override
    public Optional<SeoData.Floorplan> floorplan(String floorplanId) {
        return read("/api/floorplans/" + floorplanId + "?projection=ENRICHED", SeoData.Floorplan.class);
    }

    @Override
    public Optional<SeoData.Unit> unit(String unitId) {
        return read("/api/units/" + unitId + "?projection=UNIT_FLOORPLAN", SeoData.Unit.class);
    }

    @Override
    public List<SeoData.Faq> propertyFaqs(String propertyId) {
        return readList("/api/properties/" + propertyId + "/faqs", new TypeReference<>() {});
    }

    @Override
    public List<SeoData.Faq> floorplanFaqs(String floorplanId) {
        return readList("/api/floorplans/" + floorplanId + "/faqs", new TypeReference<>() {});
    }

    @Override
    public List<SeoData.Sublet> sublets() {
        return readList("/api/sublets", new TypeReference<>() {});
    }

    @Override
    public Optional<SeoData.Sublet> sublet(String assetKey) {
        return read("/api/sublets/" + assetKey, SeoData.Sublet.class);
    }

    @Override
    public List<SeoData.Job> jobs() {
        return readList("/api/jobVacancies", new TypeReference<>() {});
    }

    @Override
    public Optional<SeoData.Job> job(long id) {
        return read("/api/jobVacancies/" + id, SeoData.Job.class);
    }

    private <T> Optional<T> read(String path, Class<T> type) {
        return body(path).map(json -> parse(json, mapper.constructType(type)));
    }

    private <T> List<T> readList(String path, TypeReference<List<T>> type) {
        return body(path)
                .map(json -> this.<List<T>>parse(json, mapper.getTypeFactory().constructType(type)))
                .orElse(List.of());
    }

    private <T> T parse(String json, com.fasterxml.jackson.databind.JavaType type) {
        try {
            return mapper.readValue(json, type);
        } catch (Exception e) {
            throw new SeoDataUnavailableException("Unreadable response", e);
        }
    }

    /** Empty when every reachable host says 404. Throws when no host could answer. */
    private Optional<String> body(String path) {
        return cache.get(path, () -> fetch(path));
    }

    private Optional<String> fetch(String path) {
        Exception last = null;
        for (var base : baseUrls) {
            try {
                var body = client.get().uri(base + path).retrieve().body(String.class);
                return Optional.ofNullable(body);
            } catch (HttpClientErrorException.NotFound e) {
                return Optional.empty();
            } catch (Exception e) {
                log.warn("SEO data host {} failed for {}: {}", base, path, e.getMessage());
                last = e;
            }
        }
        throw new SeoDataUnavailableException("No data host answered " + path, last);
    }
}
