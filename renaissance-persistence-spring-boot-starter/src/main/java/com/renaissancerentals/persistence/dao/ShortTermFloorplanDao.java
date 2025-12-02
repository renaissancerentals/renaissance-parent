package com.renaissancerentals.persistence.dao;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.renaissancerentals.persistence.entity.ShortTermFloorplanEntity;

public interface ShortTermFloorplanDao extends CrudRepository<ShortTermFloorplanEntity, Long> {

    Optional<ShortTermFloorplanEntity> findOneByFloorplanId(@Param("floorplanId") String floorplanId);
}
