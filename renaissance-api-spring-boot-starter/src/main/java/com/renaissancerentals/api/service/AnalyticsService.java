package com.renaissancerentals.api.service;

import com.renaissancerentals.api.error.BadRequestException;
import com.renaissancerentals.api.messaging.ContactEventRequest;
import com.renaissancerentals.persistence.dao.AnalyticsDao;
import com.renaissancerentals.persistence.dao.PropertyDao;
import com.renaissancerentals.persistence.entity.AnalyticsEntity;
import java.text.MessageFormat;
import java.time.YearMonth;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    /** The sites that are not a property: the umbrella sites a contact form can sit on. */
    static final Set<String> UMBRELLA_SITES =
            Set.of("renaissance-rentals", "apartments-in-bloomington", "bloomington-apartments");

    private final AnalyticsDao analyticsRepository;
    private final PropertyDao propertyRepository;

    /**
     * Counts a step of the contact form for a property or an umbrella site. Anyone can reach this, and a count is a row, so a
     * name that is neither is refused rather than turned into a row of its own.
     */
    @Transactional
    public void handleContactEvent(ContactEventRequest contactEvent) {
        if (!isKnownSite(contactEvent.property())) {
            throw new BadRequestException("Unknown property: " + contactEvent.property());
        }
        YearMonth yearMonth = YearMonth.now();
        String analyticsName = MessageFormat.format(
                "contact-{0}-{1}-{2}", contactEvent.type().getValue(), yearMonth, contactEvent.property());

        var analytics = analyticsRepository.findById(analyticsName).orElseGet(() -> {
            var entity = new AnalyticsEntity();
            entity.setName(analyticsName);
            entity.setType("contact");
            entity.setSubType(contactEvent.type().getValue());
            entity.setCreatedDate(yearMonth.atEndOfMonth());
            entity.setCount(0);
            entity.markNew();
            return entity;
        });
        analytics.setCount(analytics.getCount() + 1);
        analyticsRepository.save(analytics);
    }

    private boolean isKnownSite(String property) {
        return property != null && (UMBRELLA_SITES.contains(property) || propertyRepository.existsById(property));
    }
}
