package com.renaissancerentals.persistence.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.renaissancerentals.persistence.entity.PropertyKeyLogEntity;

public interface PropertyKeyLogDao extends CrudRepository<PropertyKeyLogEntity, Long> {

    List<PropertyKeyLogEntity> findByPropertyId(@Param("propertyId") String propertyId);
}
