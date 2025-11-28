package com.renaissancerentals.persistence.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.PropertyCheckEntity;

public interface PropertyCheckDao extends CrudRepository<PropertyCheckEntity, Long> {

    List<PropertyCheckEntity> findAllByEmployee(String employee);

    @Query("SELECT * FROM property_check WHERE start_date = :startDate AND employee =:employee")
    List<PropertyCheckEntity> findAllByEmployeeAndStartDate(String employee,LocalDate startDate);

    @Query("SELECT * FROM property_check WHERE start_date >= :fromDate AND start_date <=:toDate")
    List<PropertyCheckEntity> findBetween(LocalDate fromDate,LocalDate toDate);
}
