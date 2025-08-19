package com.renaissancerentals.persistence.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.SubletEntity;

public interface SubletDao extends CrudRepository<SubletEntity, Long> {

    List<SubletEntity> findByActiveTrue();

    List<SubletEntity> findByActiveTrueAndApprovedTrue();

}
