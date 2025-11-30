package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.LeaseEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface LeaseDao extends CrudRepository<LeaseEntity, Long> {

    List<LeaseEntity> findByUnitIdOrderByStartDateDesc(String unitId);

    List<LeaseEntity> findByUnitIdOrderByEndDateDesc(String unitId);

    @Query("""
            SELECT * from lease
            WHERE end_data >=CURRENT_DATE
            ORDER BY end_date DESC
            """)
    List<LeaseEntity> findAllActiveLeasesOrderByEndDateDesc();
}
