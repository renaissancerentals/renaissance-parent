package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.PropertyFaqEntity;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PropertyFaqDao extends CrudRepository<PropertyFaqEntity, Long> {
    List<PropertyFaqEntity> findAllByPropertyId(@Param("propertyId") String propertyId);
}
