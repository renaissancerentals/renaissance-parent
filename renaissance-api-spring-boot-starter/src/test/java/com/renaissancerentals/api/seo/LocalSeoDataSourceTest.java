package com.renaissancerentals.api.seo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.renaissancerentals.api.domain.Faq;
import com.renaissancerentals.api.domain.JobVacancy;
import com.renaissancerentals.api.domain.Sublet;
import com.renaissancerentals.api.domain.enumeration.EmploymentType;
import com.renaissancerentals.api.domain.projection.FloorplanListing;
import com.renaissancerentals.api.domain.projection.PropertyDetails;
import com.renaissancerentals.api.domain.projection.PropertyListing;
import com.renaissancerentals.api.domain.projection.UnitListing;
import com.renaissancerentals.api.error.NotFoundException;
import com.renaissancerentals.api.repository.JobVacancyRepository;
import com.renaissancerentals.api.repository.SubletRepository;
import com.renaissancerentals.api.service.FloorplanService;
import com.renaissancerentals.api.service.PropertyService;
import com.renaissancerentals.foundation.seo.SeoDataUnavailableException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class LocalSeoDataSourceTest {

    private final PropertyService propertyService = mock(PropertyService.class);
    private final FloorplanService floorplanService = mock(FloorplanService.class);
    private final SubletRepository subletRepository = mock(SubletRepository.class);
    private final JobVacancyRepository jobRepository = mock(JobVacancyRepository.class);
    private final LocalSeoDataSource source = new LocalSeoDataSource(
            propertyService,
            floorplanService,
            subletRepository,
            jobRepository,
            new ObjectMapper().registerModule(new JavaTimeModule()));

    @Test
    void propertyDetailsAreConvertedAndExtraFieldsDropped() {
        when(propertyService.getProperty("high-grove"))
                .thenReturn(PropertyDetails.builder()
                        .id("high-grove")
                        .name("High Grove")
                        .zipcode("47401")
                        .htmlTitle("High Grove Apartments")
                        .analyticsCode("secret-not-needed")
                        .build());

        var property = source.property("high-grove").orElseThrow();

        assertThat(property.name()).isEqualTo("High Grove");
        assertThat(property.htmlTitle()).isEqualTo("High Grove Apartments");
        assertThat(property.zipcode()).isEqualTo("47401");
    }

    @Test
    void deactivatedOrMissingRecordsAreEmptyNotAnOutage() {
        when(propertyService.getProperty("gone")).thenThrow(new NotFoundException("gone"));
        when(floorplanService.getFloorplan("gone")).thenThrow(new NotFoundException("gone"));
        when(floorplanService.getUnitFloorplan("gone")).thenThrow(new NotFoundException("gone"));

        assertThat(source.property("gone")).isEmpty();
        assertThat(source.floorplan("gone")).isEmpty();
        assertThat(source.unit("gone")).isEmpty();
    }

    @Test
    void databaseFailuresAreReportedAsUnavailable() {
        when(propertyService.getProperty("x")).thenThrow(new IllegalStateException("db down"));
        when(propertyService.getPropertyListings()).thenThrow(new IllegalStateException("db down"));

        assertThatThrownBy(() -> source.property("x")).isInstanceOf(SeoDataUnavailableException.class);
        assertThatThrownBy(source::listings).isInstanceOf(SeoDataUnavailableException.class);
    }

    @Test
    void listingsKeepTheFloorplanAndUnitIdsNeededForTheSitemap() {
        when(propertyService.getPropertyListings())
                .thenReturn(List.of(PropertyListing.builder()
                        .id("high-grove")
                        .name("High Grove")
                        .floorplans(List.of(FloorplanListing.builder()
                                .id("hg-2br")
                                .name("Two Bedroom")
                                .units(List.of(UnitListing.builder()
                                        .id("u1")
                                        .rent(1150f)
                                        .build()))
                                .build()))
                        .build()));

        var listing = source.listings().get(0);

        assertThat(listing.floorplans().get(0).id()).isEqualTo("hg-2br");
        assertThat(listing.floorplans().get(0).units().get(0).id()).isEqualTo("u1");
    }

    @Test
    void faqsSubletsAndJobsAreConverted() {
        when(propertyService.getPropertyFaqs("high-grove")).thenReturn(List.of(new Faq(1L, "Pets?", "Yes", 1f)));
        when(subletRepository.getSublet("k1"))
                .thenReturn(Optional.of(Sublet.builder()
                        .assetKey("k1")
                        .title("Sunny room")
                        .createdDate(LocalDateTime.of(2026, 1, 2, 3, 4))
                        .build()));
        when(jobRepository.getJobVacancy(7L))
                .thenReturn(Optional.of(JobVacancy.builder()
                        .id(7L)
                        .title("Leasing Agent")
                        .employmentType(EmploymentType.FULL_TIME)
                        .datePosted(LocalDate.of(2026, 2, 3))
                        .build()));

        assertThat(source.propertyFaqs("high-grove").get(0).question()).isEqualTo("Pets?");
        assertThat(source.sublet("k1").orElseThrow().createdDate()).isEqualTo(LocalDateTime.of(2026, 1, 2, 3, 4));
        var job = source.job(7).orElseThrow();
        assertThat(job.employmentType()).isEqualTo("FULL_TIME");
        assertThat(job.datePosted()).isEqualTo(LocalDate.of(2026, 2, 3));
        assertThat(source.sublet("nope")).isEmpty();
    }

    @Test
    void newFactsSurviveTheConversion() {
        when(floorplanService.getFloorplan("dorset"))
                .thenReturn(com.renaissancerentals.api.domain.Floorplan.builder()
                        .id("dorset")
                        .name("Dorset")
                        .allowedPet("SMALL_DOG_CAT")
                        .petPolicy("Ground level only")
                        .highlights("Large living space")
                        .utilities(List.of(
                                new com.renaissancerentals.api.domain.Utility(1L, "water", "INCLUDED_UTILITY", null),
                                new com.renaissancerentals.api.domain.Utility(2L, "electric", "RESIDENT_UTILITY", 90f)))
                        .build());
        when(propertyService.getProperty("high-grove"))
                .thenReturn(PropertyDetails.builder()
                        .id("high-grove")
                        .leaseType("YEARLY")
                        .busRoutes(List.of(com.renaissancerentals.api.domain.PropertyBusRoute.builder()
                                .busRoute("Bloomington Transit #9")
                                .busRouteLink("https://bt.example/9")
                                .build()))
                        .build());

        var floorplan = source.floorplan("dorset").orElseThrow();
        assertThat(floorplan.allowedPet()).isEqualTo("SMALL_DOG_CAT");
        assertThat(floorplan.petPolicy()).isEqualTo("Ground level only");
        assertThat(floorplan.highlights()).isEqualTo("Large living space");
        assertThat(floorplan.utilities()).extracting("type").containsExactly("INCLUDED_UTILITY", "RESIDENT_UTILITY");

        var property = source.property("high-grove").orElseThrow();
        assertThat(property.leaseType()).isEqualTo("YEARLY");
        assertThat(property.busRoutes().get(0).busRoute()).isEqualTo("Bloomington Transit #9");
    }
}
