package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.UnitKeyLogEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface UnitKeyLogDao extends CrudRepository<UnitKeyLogEntity, Long> {

    UnitKeyLogEntity findOneByUnitId(@Param("unitId") String unitId);
}
