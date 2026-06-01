package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.ContactEmailEntity;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface ContactEmailDao extends CrudRepository<ContactEmailEntity, UUID> {}
