package com.renaissancerentals.persistence.dao;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.renaissancerentals.persistence.entity.UnitKeyLogEntity;

public interface UnitKeyLogDao extends CrudRepository<UnitKeyLogEntity, Long> {

    UnitKeyLogEntity findOneByUnitId(@Param("unitId") String unitId);
}
