package com.renaissancerentals.api.service;

import java.text.MessageFormat;
import java.time.YearMonth;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.renaissancerentals.api.messaging.ContactEventRequest;
import com.renaissancerentals.persistence.dao.AnalyticsDao;
import com.renaissancerentals.persistence.entity.AnalyticsEntity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AnalyticsService {

    private final AnalyticsDao analyticsRepository;

    @Transactional
    public void handleContactEvent(ContactEventRequest contactEvent){
        YearMonth yearMonth = YearMonth.now();
        String analyticsName = MessageFormat.format("contact-{0}-{1}-{2}",contactEvent.type().getValue(),yearMonth,
                contactEvent.property());

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

}
