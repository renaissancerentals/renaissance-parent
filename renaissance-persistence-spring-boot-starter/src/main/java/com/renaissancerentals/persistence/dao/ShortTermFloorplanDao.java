package com.renaissancerentals.persistence.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.renaissancerentals.persistence.entity.ShortTermFloorplanEntity;

public interface ShortTermFloorplanDao extends CrudRepository<ShortTermFloorplanEntity, Long> {

    @Transactional(readOnly = true)
    ShortTermFloorplanEntity findOneByFloorplanId(String floorplanId);
}
