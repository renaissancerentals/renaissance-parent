package com.renaissancerentals.persistence.dao;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.renaissancerentals.persistence.entity.AmenityEntity;

public interface AmenityDao extends CrudRepository<AmenityEntity, Long> {
    List<AmenityEntity> findByFloorplanId(@Param("floorplanId") String floorplanId);

    @Query("""
            SELECT DISTINCT type, name
                FROM amenity
                ORDER BY type, name
            """)
    List<AmenityEntity> findDistinctTypeAndName();
}
