package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.WebSpecialEntity;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WebSpecialDao extends CrudRepository<WebSpecialEntity, Long> {

    List<WebSpecialEntity> findAllByFloorplanId(@Param("floorplanId") String floorplanId);

    @Query("SELECT * FROM web_special WHERE start_date<=current_date AND end_date>=current_date AND floorplan_id=:floorplanId")
    List<WebSpecialEntity> findActiveByFloorplanId(@Param("floorplanId") String floorplanId);
}
