package com.renaissancerentals.persistence.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.renaissancerentals.persistence.entity.FloorplanFaqEntity;

public interface FloorplanFaqDao extends CrudRepository<FloorplanFaqEntity, Long> {

    List<FloorplanFaqEntity> findAllByFloorplanId(@Param("floorplanId") String floorplanId);
}
