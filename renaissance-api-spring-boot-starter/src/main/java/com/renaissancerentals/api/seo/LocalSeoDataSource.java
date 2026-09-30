package com.renaissancerentals.api.seo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.renaissancerentals.api.error.NotFoundException;
import com.renaissancerentals.api.repository.JobVacancyRepository;
import com.renaissancerentals.api.repository.SubletRepository;
import com.renaissancerentals.api.service.FloorplanService;
import com.renaissancerentals.api.service.PropertyService;
import com.renaissancerentals.foundation.seo.SeoData;
import com.renaissancerentals.foundation.seo.SeoDataSource;
import com.renaissancerentals.foundation.seo.SeoDataUnavailableException;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * SEO data straight from this site's own services, for the sites that have a database. The public API JSON shape is
 * the SEO shape, so records are converted through Jackson and unknown fields are dropped.
 */
public class LocalSeoDataSource implements SeoDataSource {

    private final PropertyService propertyService;
    private final FloorplanService floorplanService;
    private final SubletRepository subletRepository;
    private final JobVacancyRepository jobVacancyRepository;
    private final ObjectMapper mapper;

    public LocalSeoDataSource(
            PropertyService propertyService,
            FloorplanService floorplanService,
            SubletRepository subletRepository,
            JobVacancyRepository jobVacancyRepository,
            ObjectMapper mapper) {
        this.propertyService = propertyService;
        this.floorplanService = floorplanService;
        this.subletRepository = subletRepository;
        this.jobVacancyRepository = jobVacancyRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<SeoData.Property> property(String propertyId) {
        return found(() -> propertyService.getProperty(propertyId), SeoData.Property.class);
    }

    @Override
    public List<SeoData.Listing> listings() {
        return convertAll(guard(propertyService::getPropertyListings), SeoData.Listing.class);
    }

    @Override
    public Optional<SeoData.Floorplan> floorplan(String floorplanId) {
        return found(() -> floorplanService.getFloorplan(floorplanId), SeoData.Floorplan.class);
    }

    @Override
    public Optional<SeoData.Unit> unit(String unitId) {
        return found(() -> floorplanService.getUnitFloorplan(unitId), SeoData.Unit.class);
    }

    @Override
    public List<SeoData.Faq> propertyFaqs(String propertyId) {
        return convertAll(guard(() -> propertyService.getPropertyFaqs(propertyId)), SeoData.Faq.class);
    }

    @Override
    public List<SeoData.Faq> floorplanFaqs(String floorplanId) {
        return convertAll(guard(() -> floorplanService.findFloorplanFaqs(floorplanId)), SeoData.Faq.class);
    }

    @Override
    public List<SeoData.Sublet> sublets() {
        return convertAll(guard(subletRepository::getAll), SeoData.Sublet.class);
    }

    @Override
    public Optional<SeoData.Sublet> sublet(String assetKey) {
        return guard(() -> subletRepository.getSublet(assetKey)).map(s -> convert(s, SeoData.Sublet.class));
    }

    @Override
    public List<SeoData.Job> jobs() {
        return convertAll(guard(jobVacancyRepository::getActiveJobVacancies), SeoData.Job.class);
    }

    @Override
    public Optional<SeoData.Job> job(long id) {
        return guard(() -> jobVacancyRepository.getJobVacancy(id)).map(j -> convert(j, SeoData.Job.class));
    }

    /** A NotFoundException means the record does not exist (or is deactivated); anything else is an outage. */
    private <S, T> Optional<T> found(Supplier<S> source, Class<T> type) {
        try {
            return Optional.ofNullable(source.get()).map(value -> convert(value, type));
        } catch (NotFoundException e) {
            return Optional.empty();
        } catch (SeoDataUnavailableException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new SeoDataUnavailableException("Could not read " + type.getSimpleName(), e);
        }
    }

    private <T> T guard(Supplier<T> source) {
        try {
            return source.get();
        } catch (NotFoundException e) {
            throw new SeoDataUnavailableException("Unexpected not found", e);
        } catch (RuntimeException e) {
            throw new SeoDataUnavailableException("Could not read data", e);
        }
    }

    private <T> T convert(Object value, Class<T> type) {
        try {
            return mapper.convertValue(value, type);
        } catch (IllegalArgumentException e) {
            throw new SeoDataUnavailableException("Cannot convert to " + type.getSimpleName(), e);
        }
    }

    private <T> List<T> convertAll(List<?> values, Class<T> type) {
        return values == null
                ? List.of()
                : values.stream().map(v -> convert(v, type)).toList();
    }
}
