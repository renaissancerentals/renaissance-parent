package com.renaissancerentals.api.repository;

import com.renaissancerentals.api.domain.mapper.ContactMessageMapper;
import com.renaissancerentals.api.messaging.ContactMessageRequest;
import com.renaissancerentals.persistence.dao.ContactEmailDao;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@RequiredArgsConstructor
public class ContactRepository {

    private final ContactEmailDao contactEmailDao;
    private final ContactMessageMapper contactMapper;

    @Transactional
    public void save(ContactMessageRequest contactMessage) {

        var contactEmail = contactMapper.toEntity(contactMessage);
        contactEmail.setId(UUID.randomUUID());
        contactEmail.setCreatedAt(Instant.now());
        contactEmail.markNew();
        contactEmailDao.save(contactEmail);
    }
}
