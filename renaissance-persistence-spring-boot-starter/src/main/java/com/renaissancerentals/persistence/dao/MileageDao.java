package com.renaissancerentals.persistence.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.renaissancerentals.persistence.entity.MileageEntity;

public interface MileageDao extends CrudRepository<MileageEntity, Long> {

    List<MileageEntity> findAllByEmployee(@Param("employee") String employee);

    @Query("SELECT * FROM mileage WHERE drive_date >= :fromDate AND drive_date <=:toDate")
    List<MileageEntity> findBetween(@Param("fromDate") LocalDate fromDate,@Param("toDate") LocalDate toDate);
}
