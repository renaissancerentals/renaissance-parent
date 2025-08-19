package com.renaissancerentals.persistence.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.FloorplanVariationEntity;

public interface FloorplanVariationDao extends CrudRepository<FloorplanVariationEntity, Long> {

    List<FloorplanVariationEntity> findAllByFloorplanId(String floorplanId);
}
