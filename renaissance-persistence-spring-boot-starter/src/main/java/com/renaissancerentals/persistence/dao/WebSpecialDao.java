package com.renaissancerentals.persistence.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.WebSpecialEntity;

public interface WebSpecialDao extends CrudRepository<WebSpecialEntity, Long> {

    List<WebSpecialEntity> findAllByFloorplanId(String floorplanId);
}
