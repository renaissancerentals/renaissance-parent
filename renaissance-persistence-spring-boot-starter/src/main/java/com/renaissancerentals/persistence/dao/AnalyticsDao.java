package com.renaissancerentals.persistence.dao;

import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.AnalyticsEntity;

public interface AnalyticsDao extends CrudRepository<AnalyticsEntity, String> {

}
