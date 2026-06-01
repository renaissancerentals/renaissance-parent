package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.ApplicationEmailEntity;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface ApplicationEmailDao extends CrudRepository<ApplicationEmailEntity, UUID> {}
