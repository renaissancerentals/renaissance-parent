package com.renaissancerentals.persistence.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.renaissancerentals.persistence.entity.SubletEntity;

public interface SubletDao extends CrudRepository<SubletEntity, Long> {

    List<SubletEntity> findByActiveTrue();

    List<SubletEntity> findByActiveTrueAndApprovedTrue();

    Optional<SubletEntity> findOneByAssetKey(@Param("assetKey") String assetKey);

}
