package com.renaissancerentals.persistence.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.UtilityEntity;

public interface UtilityDao extends CrudRepository<UtilityEntity, Long> {

    List<UtilityEntity> findByFloorplanIdAndNameAndType(String floorplanId,String name,String type);

    void deleteByFloorplanId(String floorplanId);

    List<UtilityEntity> findByFloorplanId(String floorplanId);

}
