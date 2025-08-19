package com.renaissancerentals.persistence.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.PropertyKeyLogEntity;

public interface PropertyKeyLogDao extends CrudRepository<PropertyKeyLogEntity, Long> {

    List<PropertyKeyLogEntity> findByPropertyId(String propertyId);
}
