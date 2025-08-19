package com.renaissancerentals.persistence.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.PropertyCheckEntity;

public interface PropertyCheckDao extends CrudRepository<PropertyCheckEntity, Long> {

    List<PropertyCheckEntity> findAllByEmployee(String employee);

    @Query("select p from PropertyCheckEntity p where p.startDate = :startDate and p.employee =:employee")
    List<PropertyCheckEntity> findAllByEmployeeAndStartDate(String employee,LocalDate startDate);

    @Query("select p from PropertyCheckEntity p where p.startDate >= :fromDate and p.startDate <=:toDate")
    List<PropertyCheckEntity> findBetween(LocalDate fromDate,LocalDate toDate);
}
