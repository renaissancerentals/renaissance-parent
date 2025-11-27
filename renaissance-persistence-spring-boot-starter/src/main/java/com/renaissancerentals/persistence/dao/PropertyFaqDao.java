package com.renaissancerentals.persistence.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.PropertyFaqEntity;

public interface PropertyFaqDao extends CrudRepository<PropertyFaqEntity, Long> {
    List<PropertyFaqEntity> findAllByPropertyId(String propertyId);
}
