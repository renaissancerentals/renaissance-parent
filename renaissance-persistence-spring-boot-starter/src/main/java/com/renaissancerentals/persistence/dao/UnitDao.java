package com.renaissancerentals.persistence.dao;

import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.UnitEntity;

public interface UnitDao extends CrudRepository<UnitEntity, String> {

}
