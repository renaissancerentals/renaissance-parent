package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.PropertyEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface PropertyDao extends CrudRepository<PropertyEntity, String> {

    Optional<PropertyEntity> findOneByNameIgnoreCase(@Param("name") String name);

    List<PropertyEntity> findAllByLeaseType(@Param("leaseType") String leaseType);
}
