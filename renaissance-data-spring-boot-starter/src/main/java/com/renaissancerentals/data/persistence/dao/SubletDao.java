package com.renaissancerentals.data.persistence.dao;

import com.renaissancerentals.data.persistence.entity.SubletEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubletDao extends CrudRepository<SubletEntity, Long> {

    List<SubletEntity> findByActiveTrue();

    List<SubletEntity> findByActiveTrueAndApprovedTrue();

}
