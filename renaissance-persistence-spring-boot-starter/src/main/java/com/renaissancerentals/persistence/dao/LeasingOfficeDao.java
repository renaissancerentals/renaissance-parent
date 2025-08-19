package com.renaissancerentals.persistence.dao;

import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.LeasingOfficeEntity;

public interface LeasingOfficeDao extends CrudRepository<LeasingOfficeEntity, String> {

}
