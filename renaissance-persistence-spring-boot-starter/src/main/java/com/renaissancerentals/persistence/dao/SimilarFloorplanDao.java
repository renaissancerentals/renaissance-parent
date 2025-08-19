package com.renaissancerentals.persistence.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.SimilarFloorplanEntity;

public interface SimilarFloorplanDao extends CrudRepository<SimilarFloorplanEntity, Long> {

    List<SimilarFloorplanEntity> findAllByFloorplanId(String floorplanId);
}
