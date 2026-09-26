package com.renaissancerentals.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.renaissancerentals.api.domain.mapper.FaqMapper;
import com.renaissancerentals.api.domain.mapper.UnitMapper;
import com.renaissancerentals.api.domain.projection.PropertySummary;
import com.renaissancerentals.api.error.NotFoundException;
import com.renaissancerentals.persistence.dao.FloorplanDao;
import com.renaissancerentals.persistence.dao.FloorplanFaqDao;
import com.renaissancerentals.persistence.dao.FloorplanVariationDao;
import com.renaissancerentals.persistence.dao.SimilarFloorplanDao;
import com.renaissancerentals.persistence.dao.TestimonialDao;
import com.renaissancerentals.persistence.entity.FloorplanEntity;
import com.renaissancerentals.persistence.entity.SimilarFloorplanEntity;
import com.renaissancerentals.persistence.entity.TestimonialEntity;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class FloorplanServiceTest {

    @Mock
    private FloorplanDao floorplanDao;

    @Mock
    private SimilarFloorplanDao similarFloorplanDao;

    @Mock
    private FloorplanVariationDao floorplanVariationDao;

    @Mock
    private TestimonialDao testimonialDao;

    @Mock
    private FloorplanFaqDao floorplanFaqDao;

    @Mock
    private FaqMapper faqMapper;

    @Mock
    private UnitMapper unitMapper;

    @Mock
    private UnitService unitService;

    @Mock
    private PropertyService propertyService;

    @Mock
    private WebSpecialService webSpecialService;

    @Mock
    private AmenityService amenityService;

    @Mock
    private UtilityService utilityService;

    private FloorplanService floorplanService() {
        return new FloorplanService(
                floorplanDao,
                similarFloorplanDao,
                floorplanVariationDao,
                testimonialDao,
                floorplanFaqDao,
                faqMapper,
                unitMapper,
                Executors.newVirtualThreadPerTaskExecutor(),
                unitService,
                propertyService,
                webSpecialService,
                amenityService,
                utilityService);
    }

    @Test
    void getFloorplanThrowsWhenFloorplanMissing() {
        when(floorplanDao.findById("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> floorplanService().getFloorplan("missing")).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getFloorplanThrowsWhenInactive() {
        FloorplanEntity entity = new FloorplanEntity();
        entity.setId("f-1");
        entity.setActive(false);
        when(floorplanDao.findById("f-1")).thenReturn(Optional.of(entity));

        assertThatThrownBy(() -> floorplanService().getFloorplan("f-1")).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getFloorplanThrowsWhenPropertyMissing() {
        FloorplanEntity entity = new FloorplanEntity();
        entity.setId("f-1");
        entity.setActive(true);
        when(floorplanDao.findById("f-1")).thenReturn(Optional.of(entity));
        when(propertyService.getPropertySummaryForFloorplanAsync("f-1"))
                .thenReturn(CompletableFuture.completedFuture(Optional.empty()));
        when(unitService.getUnitsForFloorplanAsync("f-1")).thenReturn(CompletableFuture.completedFuture(List.of()));
        when(amenityService.getAmenitiesForFloorplanAsync("f-1"))
                .thenReturn(CompletableFuture.completedFuture(List.of()));
        when(utilityService.getUtilitiesForFloorplanAsync("f-1"))
                .thenReturn(CompletableFuture.completedFuture(List.of()));
        when(webSpecialService.getWebSpecialForFloorplanAsync("f-1"))
                .thenReturn(CompletableFuture.completedFuture(List.of()));

        assertThatThrownBy(() -> floorplanService().getFloorplan("f-1")).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getFloorplanAssemblesResultFromAllCollaborators() {
        FloorplanEntity entity = new FloorplanEntity();
        entity.setId("f-1");
        entity.setName("Floorplan One");
        entity.setBedroom(2);
        entity.setActive(true);
        PropertySummary propertySummary =
                PropertySummary.builder().id("p-1").name("Property One").build();

        when(floorplanDao.findById("f-1")).thenReturn(Optional.of(entity));
        when(propertyService.getPropertySummaryForFloorplanAsync("f-1"))
                .thenReturn(CompletableFuture.completedFuture(Optional.of(propertySummary)));
        when(unitService.getUnitsForFloorplanAsync("f-1")).thenReturn(CompletableFuture.completedFuture(List.of()));
        when(amenityService.getAmenitiesForFloorplanAsync("f-1"))
                .thenReturn(CompletableFuture.completedFuture(List.of()));
        when(utilityService.getUtilitiesForFloorplanAsync("f-1"))
                .thenReturn(CompletableFuture.completedFuture(List.of()));
        when(webSpecialService.getWebSpecialForFloorplanAsync("f-1"))
                .thenReturn(CompletableFuture.completedFuture(List.of()));

        var result = floorplanService().getFloorplan("f-1");

        assertThat(result.id()).isEqualTo("f-1");
        assertThat(result.name()).isEqualTo("Floorplan One");
        assertThat(result.bedroom()).isEqualTo(2);
        assertThat(result.property()).isEqualTo(propertySummary);
    }

    @Test
    void findSimilarFloorplansMapsEntitiesToDomain() {
        SimilarFloorplanEntity entity = new SimilarFloorplanEntity();
        entity.setSimilarFloorplanId("f-2");
        when(similarFloorplanDao.findAllByFloorplanId("f-1")).thenReturn(List.of(entity));

        var result = floorplanService().findSimilarFloorplans("f-1");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().similarFloorplanId()).isEqualTo("f-2");
    }

    @Test
    void findFloorplanTestimonialsMapsEntitiesToDomain() {
        TestimonialEntity entity = new TestimonialEntity();
        entity.setTenant("Jane Doe");
        entity.setTestimonial("Great place!");
        when(testimonialDao.findAllByFloorplanId("f-1")).thenReturn(List.of(entity));

        var result = floorplanService().findFloorplanTestimonials("f-1");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().tenant()).isEqualTo("Jane Doe");
        assertThat(result.getFirst().testimonial()).isEqualTo("Great place!");
    }
}
