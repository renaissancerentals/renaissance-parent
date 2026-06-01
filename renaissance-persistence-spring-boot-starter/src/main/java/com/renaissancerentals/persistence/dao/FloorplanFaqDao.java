package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.FloorplanFaqEntity;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface FloorplanFaqDao extends CrudRepository<FloorplanFaqEntity, Long> {

    List<FloorplanFaqEntity> findAllByFloorplanId(@Param("floorplanId") String floorplanId);
}
