package com.renaissancerentals.persistence.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.TestimonialEntity;

public interface TestimonialDao extends CrudRepository<TestimonialEntity, Long> {

    List<TestimonialEntity> findAllByFloorplanId(String floorplanId);
}
