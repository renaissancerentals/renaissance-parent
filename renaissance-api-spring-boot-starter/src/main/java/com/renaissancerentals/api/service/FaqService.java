package com.renaissancerentals.api.service;

import com.renaissancerentals.api.domain.Faq;
import com.renaissancerentals.api.domain.mapper.FaqMapper;
import com.renaissancerentals.persistence.dao.MaintenanceFaqDao;
import com.renaissancerentals.persistence.dao.ResidentFaqDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class FaqService {
    private final ResidentFaqDao residentFaqDao;
    private final MaintenanceFaqDao maintenanceFaqDao;
    private final FaqMapper faqMapper;

    public List<Faq> getResidentFaqs() {
        return StreamSupport.stream(residentFaqDao.findAll().spliterator(), false).map(faqMapper::toFaq).toList();
    }

    public List<Faq> getMaintenanceFaqs() {
        return StreamSupport.stream(maintenanceFaqDao.findAll().spliterator(), false).map(faqMapper::toFaq).toList();
    }
}
