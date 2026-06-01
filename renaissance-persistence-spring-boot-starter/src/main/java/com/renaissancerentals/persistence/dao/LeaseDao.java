package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.LeaseEntity;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface LeaseDao extends CrudRepository<LeaseEntity, Long> {

    List<LeaseEntity> findAllByOrderByEndDateDesc();

    List<LeaseEntity> findAllByOrderByEndDateAsc();

    List<LeaseEntity> findAllByUnitIdOrderByEndDateDesc(String unitId);
}
