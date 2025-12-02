package com.renaissancerentals.persistence.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.renaissancerentals.persistence.entity.FloorplanEntity;

public interface FloorplanDao extends CrudRepository<FloorplanEntity, String> {

    Optional<FloorplanEntity> findOneByNameIgnoreCase(@Param("name") String name);

    List<FloorplanEntity> findAllByActiveTrueAndFeaturedOnMainTrue();

    List<FloorplanEntity> findAllByActiveTrueAndFeaturedTrue();

    List<FloorplanEntity> findByPropertyId(@Param("propertyId") String propertyId);

}
