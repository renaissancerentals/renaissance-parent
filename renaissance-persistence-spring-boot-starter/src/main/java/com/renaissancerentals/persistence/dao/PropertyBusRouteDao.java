package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.PropertyBusRouteEntity;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PropertyBusRouteDao extends CrudRepository<PropertyBusRouteEntity, Long> {

    List<PropertyBusRouteEntity> findByPropertyId(@Param("propertyId") String propertyId);
}
