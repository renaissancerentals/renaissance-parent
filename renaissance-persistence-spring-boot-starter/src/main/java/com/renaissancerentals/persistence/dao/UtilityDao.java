package com.renaissancerentals.persistence.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.renaissancerentals.persistence.entity.UtilityEntity;

public interface UtilityDao extends CrudRepository<UtilityEntity, Long> {

    List<UtilityEntity> findByFloorplanIdAndNameAndType(@Param("floorplanId") String floorplanId,
            @Param("name") String name,@Param("type") String type);

    void deleteByFloorplanId(@Param("floorplanId") String floorplanId);

    List<UtilityEntity> findByFloorplanId(@Param("floorplanId") String floorplanId);

}
