package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.PropertyCheckEntity;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PropertyCheckDao extends CrudRepository<PropertyCheckEntity, Long> {

    List<PropertyCheckEntity> findAllByEmployee(@Param("employee") String employee);

    @Query("SELECT * FROM property_check WHERE start_date = :startDate AND employee =:employee")
    List<PropertyCheckEntity> findAllByEmployeeAndStartDate(
            @Param("employee") String employee, @Param("startDate") LocalDate startDate);

    @Query("SELECT * FROM property_check WHERE start_date >= :fromDate AND start_date <=:toDate")
    List<PropertyCheckEntity> findBetween(@Param("fromDate") LocalDate fromDate, @Param("toDate") LocalDate toDate);
}
