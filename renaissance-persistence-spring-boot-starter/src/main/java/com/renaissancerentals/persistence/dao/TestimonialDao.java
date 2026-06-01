package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.TestimonialEntity;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TestimonialDao extends CrudRepository<TestimonialEntity, Long> {

    List<TestimonialEntity> findAllByFloorplanId(@Param("floorplanId") String floorplanId);
}
