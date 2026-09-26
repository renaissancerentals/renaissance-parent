package com.renaissancerentals.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.renaissancerentals.api.config.PropertyConfigProperties;
import com.renaissancerentals.api.domain.TeamMember;
import com.renaissancerentals.api.domain.mapper.FaqMapper;
import com.renaissancerentals.api.domain.mapper.PropertyMapper;
import com.renaissancerentals.api.domain.projection.FloorplanListing;
import com.renaissancerentals.api.domain.projection.PropertyContact;
import com.renaissancerentals.api.domain.projection.PropertyDetails;
import com.renaissancerentals.api.domain.projection.PropertyListing;
import com.renaissancerentals.api.error.NotFoundException;
import com.renaissancerentals.api.repository.PropertyRepository;
import com.renaissancerentals.persistence.dao.PropertyDao;
import com.renaissancerentals.persistence.dao.PropertyFaqDao;
import com.renaissancerentals.persistence.entity.PropertyEntity;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PropertyServiceTest {

    @Mock
    private PropertyDao propertyDao;

    @Mock
    private PropertyMapper propertyMapper;

    @Mock
    private PropertyRepository propertyRepository;

    @Mock
    private PropertyFaqDao propertyFaqDao;

    @Mock
    private FaqMapper faqMapper;

    private final PropertyConfigProperties propertyConfigProperties = new PropertyConfigProperties(
            "default@example.com",
            "8125551234",
            "Default Manager",
            "https://www.renaissancerentals.com/",
            Map.of("summer-house", "https://www.summerhouseatindiana.com/"));

    private PropertyService propertyService() {
        return new PropertyService(
                propertyDao,
                propertyMapper,
                propertyRepository,
                propertyFaqDao,
                propertyConfigProperties,
                faqMapper,
                Executors.newVirtualThreadPerTaskExecutor());
    }

    @Test
    void getPropertyContactReturnsMappedContactWhenPropertyExists() {
        PropertyEntity entity = new PropertyEntity();
        PropertyContact mapped = new PropertyContact("Summer House", "sh@example.com", null, "8125559999");
        when(propertyDao.findById("summer-house")).thenReturn(Optional.of(entity));
        when(propertyMapper.toPropertyContact(entity)).thenReturn(mapped);

        PropertyContact result = propertyService().getPropertyContact("summer-house");

        assertThat(result).isEqualTo(mapped);
    }

    @Test
    void getPropertyContactFallsBackToDefaultsWhenPropertyMissing() {
        when(propertyDao.findById("missing")).thenReturn(Optional.empty());

        PropertyContact result = propertyService().getPropertyContact("missing");

        assertThat(result.propertyName()).isEqualTo("Renaissance Rentals");
        assertThat(result.email()).isEqualTo("default@example.com");
        assertThat(result.phone()).isEqualTo("8125551234");
    }

    @Test
    void getPropertyManagerFallsBackToDefaultManagerWhenNoneAssigned() {
        when(propertyRepository.getPropertyManager("p-1")).thenReturn(Optional.empty());

        TeamMember result = propertyService().getPropertyManager("p-1");

        assertThat(result.getName()).isEqualTo("Default Manager");
        assertThat(result.getEmail()).isEqualTo("default@example.com");
    }

    @Test
    void getPropertyManagerReturnsAssignedManagerWhenPresent() {
        TeamMember manager = TeamMember.builder().name("Alice").build();
        when(propertyRepository.getPropertyManager("p-1")).thenReturn(Optional.of(manager));

        assertThat(propertyService().getPropertyManager("p-1")).isEqualTo(manager);
    }

    @Test
    void getPropertyUrlReturnsConfiguredUrlWhenPresent() {
        assertThat(propertyService().getPropertyUrl("summer-house")).isEqualTo("https://www.summerhouseatindiana.com/");
    }

    @Test
    void getPropertyUrlFallsBackToDefaultUrlWhenNotConfigured() {
        assertThat(propertyService().getPropertyUrl("unknown-property"))
                .isEqualTo("https://www.renaissancerentals.com/");
    }

    @Test
    void getPropertyThrowsNotFoundWhenPropertyMissing() {
        when(propertyRepository.getProperty("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> propertyService().getProperty("missing")).isInstanceOf(NotFoundException.class);
    }

    @Test
    void getPropertyReturnsDetailsWhenPresent() {
        PropertyDetails details = PropertyDetails.builder().id("p-1").build();
        when(propertyRepository.getProperty("p-1")).thenReturn(Optional.of(details));

        assertThat(propertyService().getProperty("p-1")).isEqualTo(details);
    }

    @Test
    void getFloorplanListingsForPropertyReturnsEmptyListWhenPropertyListingMissing() {
        when(propertyRepository.getPropertyListing("missing")).thenReturn(Optional.empty());

        assertThat(propertyService().getFloorplanListingsForProperty("missing")).isEmpty();
    }

    @Test
    void getFloorplanListingsForPropertyReturnsFloorplansWhenPresent() {
        FloorplanListing floorplanListing = FloorplanListing.builder().id("f-1").build();
        PropertyListing listing = PropertyListing.builder()
                .id("p-1")
                .floorplans(List.of(floorplanListing))
                .build();
        when(propertyRepository.getPropertyListing("p-1")).thenReturn(Optional.of(listing));

        assertThat(propertyService().getFloorplanListingsForProperty("p-1")).containsExactly(floorplanListing);
    }
}
