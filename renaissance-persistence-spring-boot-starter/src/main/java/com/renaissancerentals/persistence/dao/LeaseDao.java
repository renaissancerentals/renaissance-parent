package com.renaissancerentals.persistence.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.LeaseEntity;

public interface LeaseDao extends CrudRepository<LeaseEntity, Long> {

    List<LeaseEntity> findAllByOrderByEndDateDesc();

    List<LeaseEntity> findAllByOrderByEndDateAsc();
}
