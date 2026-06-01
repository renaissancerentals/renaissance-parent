package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.FloorplanVariationEntity;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface FloorplanVariationDao extends CrudRepository<FloorplanVariationEntity, Long> {

    List<FloorplanVariationEntity> findAllByFloorplanId(@Param("floorplanId") String floorplanId);
}
