package com.renaissancerentals.persistence.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.ShortTermFloorplanEntity;

public interface ShortTermFloorplanDao extends CrudRepository<ShortTermFloorplanEntity, Long> {

    Optional<ShortTermFloorplanEntity> findOneByFloorplanId(String floorplanId);
}
