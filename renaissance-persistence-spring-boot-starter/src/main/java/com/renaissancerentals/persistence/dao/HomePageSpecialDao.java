package com.renaissancerentals.persistence.dao;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.HomePageSpecialEntity;

public interface HomePageSpecialDao extends CrudRepository<HomePageSpecialEntity, Long> {

    @Query("SELECT * FROM home_page_special WHERE CURRENT_DATE BETWEEN start_date AND end_date")
    List<HomePageSpecialEntity> findAllActive();
}
