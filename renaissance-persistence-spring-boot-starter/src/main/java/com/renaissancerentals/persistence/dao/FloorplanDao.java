package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.FloorplanEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface FloorplanDao extends CrudRepository<FloorplanEntity, String> {

    Optional<FloorplanEntity> findOneByNameIgnoreCase(@Param("name") String name);

    List<FloorplanEntity> findAllByActiveTrueAndFeaturedOnMainTrue();

    List<FloorplanEntity> findAllByActiveTrueAndFeaturedTrue();

    List<FloorplanEntity> findByPropertyId(@Param("propertyId") String propertyId);
}
