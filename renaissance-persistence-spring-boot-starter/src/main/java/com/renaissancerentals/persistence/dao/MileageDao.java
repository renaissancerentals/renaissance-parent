package com.renaissancerentals.persistence.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.MileageEntity;

public interface MileageDao extends CrudRepository<MileageEntity, Long> {

    List<MileageEntity> findAllByEmployee(String employee);

    @Query("select m from MileageEntity m where m.driveDate >= :fromDate and m.driveDate <=:toDate")
    List<MileageEntity> findBetween(LocalDate fromDate,LocalDate toDate);
}
