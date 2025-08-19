package com.renaissancerentals.persistence.dao;

import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.AmenityEntity;

public interface AmenityDao extends CrudRepository<AmenityEntity, Long> {

}
