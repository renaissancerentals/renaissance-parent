package com.renaissancerentals.persistence.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.PropertyEntity;

public interface PropertyDao extends CrudRepository<PropertyEntity, String> {

    Optional<PropertyEntity> findOneByNameIgnoreCase(String name);

    List<PropertyEntity> findAllByLeaseType(String leaseType);

}
