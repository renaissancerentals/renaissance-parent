package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.PropertyKeyLogEntity;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PropertyKeyLogDao extends CrudRepository<PropertyKeyLogEntity, Long> {

    List<PropertyKeyLogEntity> findByPropertyId(@Param("propertyId") String propertyId);
}
