package com.renaissancerentals.persistence.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.FloorplanFaqEntity;

public interface FloorplanFaqDao extends CrudRepository<FloorplanFaqEntity, Long> {

    List<FloorplanFaqEntity> findAllByFloorplanId(String floorplanId);
}
