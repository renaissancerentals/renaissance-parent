package com.renaissancerentals.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.renaissancerentals.api.domain.projection.FloorplanDetails;
import com.renaissancerentals.api.domain.projection.PropertySummary;
import com.renaissancerentals.api.error.NotFoundException;
import com.renaissancerentals.api.repository.FloorplanRepository;
import com.renaissancerentals.api.repository.PropertyRepository;
import com.renaissancerentals.persistence.dao.ShortTermFloorplanDao;
import com.renaissancerentals.persistence.entity.ShortTermFloorplanEntity;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShortTermServiceTest {

    @Mock
    private FloorplanRepository floorplanRepository;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private ShortTermFloorplanDao shortTermFloorplanDao;

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    private ShortTermService shortTermService() {
        return new ShortTermService(floorplanRepository, propertyRepository, shortTermFloorplanDao, executor);
    }

    @Test
    void getShortTermFloorplanMergesFloorplanPropertyAndPricingData() {
        String floorplanId = "f-1";
        PropertySummary propertySummary =
                PropertySummary.builder().id("p-1").name("Property One").build();
        FloorplanDetails floorplanDetails = FloorplanDetails.builder()
                .id(floorplanId)
                .name("Floorplan One")
                .bedroom(2)
                .build();
        ShortTermFloorplanEntity shortTermEntity = new ShortTermFloorplanEntity();
        shortTermEntity.setFloorplanId(floorplanId);
        shortTermEntity.setSquareFoot(750);

        when(propertyRepository.getPropertySummaryForFloorplan(floorplanId)).thenReturn(Optional.of(propertySummary));
        when(floorplanRepository.getFloorplanDetails(floorplanId)).thenReturn(Optional.of(floorplanDetails));
        when(shortTermFloorplanDao.findOneByFloorplanId(floorplanId)).thenReturn(Optional.of(shortTermEntity));

        var result = shortTermService().getShortTermFloorplan(floorplanId);

        assertThat(result.id()).isEqualTo(floorplanId);
        assertThat(result.name()).isEqualTo("Floorplan One");
        assertThat(result.bedroom()).isEqualTo(2);
        assertThat(result.property()).isEqualTo(propertySummary);
        assertThat(result.squareFoot()).isEqualTo(750);
    }

    @Test
    void getShortTermFloorplanThrowsWhenPropertyMissing() {
        String floorplanId = "f-missing";
        when(propertyRepository.getPropertySummaryForFloorplan(floorplanId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shortTermService().getShortTermFloorplan(floorplanId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getShortTermFloorplanThrowsWhenFloorplanDetailsMissing() {
        String floorplanId = "f-1";
        when(propertyRepository.getPropertySummaryForFloorplan(floorplanId))
                .thenReturn(Optional.of(PropertySummary.builder().id("p-1").build()));
        when(floorplanRepository.getFloorplanDetails(floorplanId)).thenReturn(Optional.empty());
        when(shortTermFloorplanDao.findOneByFloorplanId(floorplanId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shortTermService().getShortTermFloorplan(floorplanId))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getShortTermFloorplansForPropertySkipsEntriesWithNoMatchingFloorplanDetails() {
        String propertyId = "p-1";
        PropertySummary propertySummary =
                PropertySummary.builder().id(propertyId).name("Property One").build();
        FloorplanDetails matchingDetails =
                FloorplanDetails.builder().id("f-1").name("Floorplan One").build();

        ShortTermFloorplanEntity matching = new ShortTermFloorplanEntity();
        matching.setFloorplanId("f-1");
        ShortTermFloorplanEntity orphaned = new ShortTermFloorplanEntity();
        orphaned.setFloorplanId("f-orphaned");

        when(propertyRepository.getPropertySummaryForProperty(propertyId)).thenReturn(Optional.of(propertySummary));
        when(floorplanRepository.getFloorplanDetailsForProperty(propertyId)).thenReturn(List.of(matchingDetails));
        when(shortTermFloorplanDao.findByPropertyId(propertyId)).thenReturn(List.of(matching, orphaned));

        var result = shortTermService().getShortTermFloorplansForProperty(propertyId);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo("f-1");
    }

    @Test
    void getShortTermFloorplansForPropertyThrowsWhenPropertyMissing() {
        String propertyId = "missing";
        when(propertyRepository.getPropertySummaryForProperty(propertyId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> shortTermService().getShortTermFloorplansForProperty(propertyId))
                .isInstanceOf(NotFoundException.class);
    }
}
