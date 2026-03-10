package com.renaissancerentals.persistence.dao;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.UnitEntity;

public interface UnitDao extends CrudRepository<UnitEntity, String> {

    @Query(value = "SELECT * FROM unit u \n" + "    JOIN floorplan f ON f.id=u.floorplan_id\n"
            + "    JOIN property p ON p.id=f.property_id \n"
            + "WHERE u.active=true AND f.active=true AND p.active=true")
    List<UnitEntity> findAllActive();

    List<UnitEntity> findAllByFloorplanId(String floorplanId);

}
