package com.renaissancerentals.persistence.dao;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.renaissancerentals.persistence.entity.PropertyAmenityEntity;

public interface PropertyAmenityDao extends CrudRepository<PropertyAmenityEntity, Long> {

    @Query("""
            SELECT DISTINCT type, name
                FROM property_amenity
                ORDER BY type, name
            """)
    List<PropertyAmenityEntity> findDistinctTypeAndName();

    List<PropertyAmenityEntity> findByPropertyId(@Param("propertyId") String propertyId);
}
