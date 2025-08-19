package com.renaissancerentals.persistence.dao;

import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.PropertyAmenityEntity;

public interface PropertyAmenityDao extends CrudRepository<PropertyAmenityEntity, Long> {

}
