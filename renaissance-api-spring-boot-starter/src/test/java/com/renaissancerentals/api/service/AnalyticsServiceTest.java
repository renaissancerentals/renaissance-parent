package com.renaissancerentals.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.renaissancerentals.api.domain.enumeration.ContactEventType;
import com.renaissancerentals.api.error.BadRequestException;
import com.renaissancerentals.api.messaging.ContactEventRequest;
import com.renaissancerentals.persistence.dao.AnalyticsDao;
import com.renaissancerentals.persistence.dao.PropertyDao;
import com.renaissancerentals.persistence.entity.AnalyticsEntity;
import java.time.YearMonth;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private AnalyticsDao analyticsDao;

    @Mock
    private PropertyDao propertyDao;

    private AnalyticsService service() {
        return new AnalyticsService(analyticsDao, propertyDao);
    }

    @Test
    void countsAStepForARealProperty() {
        var name = "contact-clicked-" + YearMonth.now() + "-verona-park";
        when(propertyDao.existsById("verona-park")).thenReturn(true);
        when(analyticsDao.findById(name)).thenReturn(Optional.empty());

        service().handleContactEvent(new ContactEventRequest("verona-park", ContactEventType.CLICKED));

        var saved = ArgumentCaptor.forClass(AnalyticsEntity.class);
        verify(analyticsDao).save(saved.capture());
        assertThat(saved.getValue().getName()).isEqualTo(name);
        assertThat(saved.getValue().getCount()).isEqualTo(1);
    }

    @Test
    void addsToTheCountThereAlready() {
        var name = "contact-submitted-" + YearMonth.now() + "-verona-park";
        var existing = new AnalyticsEntity();
        existing.setName(name);
        existing.setCount(4);
        when(propertyDao.existsById("verona-park")).thenReturn(true);
        when(analyticsDao.findById(name)).thenReturn(Optional.of(existing));

        service().handleContactEvent(new ContactEventRequest("verona-park", ContactEventType.SUBMITTED));

        verify(analyticsDao).save(existing);
        assertThat(existing.getCount()).isEqualTo(5);
    }

    @Test
    void countsAnUmbrellaSiteWithoutAskingTheProperties() {
        when(analyticsDao.findById(any())).thenReturn(Optional.empty());

        service().handleContactEvent(new ContactEventRequest("renaissance-rentals", ContactEventType.INITIATED));

        verify(propertyDao, never()).existsById(any());
        verify(analyticsDao).save(any());
    }

    @Test
    void refusesAWordThatIsNotAPropertyAndWritesNothing() {
        when(propertyDao.existsById("ready")).thenReturn(false);

        assertThatThrownBy(
                        () -> service().handleContactEvent(new ContactEventRequest("ready", ContactEventType.CLICKED)))
                .isInstanceOf(BadRequestException.class)
                .satisfies(e -> {
                    var error = ((BadRequestException) e).getErrorMessage();
                    assertThat(error.message()).contains("ready");
                    assertThat(error.status().value()).isEqualTo(400);
                });

        verify(analyticsDao, never()).save(any());
    }

    @Test
    void refusesAnEventWithNoProperty() {
        assertThatThrownBy(() -> service().handleContactEvent(new ContactEventRequest(null, ContactEventType.CLICKED)))
                .isInstanceOf(BadRequestException.class);

        verify(analyticsDao, never()).save(any());
    }
}
