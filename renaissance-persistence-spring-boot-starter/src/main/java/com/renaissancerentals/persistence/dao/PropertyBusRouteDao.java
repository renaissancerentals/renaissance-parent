package com.renaissancerentals.persistence.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.PropertyBusRouteEntity;

public interface PropertyBusRouteDao extends CrudRepository<PropertyBusRouteEntity, Long> {

    List<PropertyBusRouteEntity> findByPropertyId(String propertyId);
}
