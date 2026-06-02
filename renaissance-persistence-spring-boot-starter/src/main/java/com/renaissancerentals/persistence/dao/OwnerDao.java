package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.OwnerEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OwnerDao extends CrudRepository<OwnerEntity, Long> {}
