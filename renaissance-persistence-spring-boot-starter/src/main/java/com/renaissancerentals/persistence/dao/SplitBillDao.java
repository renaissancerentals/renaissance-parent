package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.SplitBillEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SplitBillDao extends CrudRepository<SplitBillEntity, Long> {}
