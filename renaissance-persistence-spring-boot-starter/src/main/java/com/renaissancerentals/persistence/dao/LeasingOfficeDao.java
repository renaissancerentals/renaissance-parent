package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.LeasingOfficeEntity;
import org.springframework.data.repository.CrudRepository;

public interface LeasingOfficeDao extends CrudRepository<LeasingOfficeEntity, String> {}
