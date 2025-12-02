package com.renaissancerentals.persistence.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.renaissancerentals.persistence.entity.TestimonialEntity;

public interface TestimonialDao extends CrudRepository<TestimonialEntity, Long> {

    List<TestimonialEntity> findAllByFloorplanId(@Param("floorplanId") String floorplanId);
}
