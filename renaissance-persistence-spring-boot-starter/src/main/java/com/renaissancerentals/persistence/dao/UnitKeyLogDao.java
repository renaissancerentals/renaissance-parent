package com.renaissancerentals.persistence.dao;

import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.UnitKeyLogEntity;

public interface UnitKeyLogDao extends CrudRepository<UnitKeyLogEntity, Long> {

    UnitKeyLogEntity findOneByUnitId(String unitId);
}
