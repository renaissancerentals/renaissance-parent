package com.renaissancerentals.persistence.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.renaissancerentals.persistence.entity.WebSpecialEntity;

public interface WebSpecialDao extends CrudRepository<WebSpecialEntity, Long> {

    List<WebSpecialEntity> findAllByFloorplanId(@Param("floorplanId") String floorplanId);
}
