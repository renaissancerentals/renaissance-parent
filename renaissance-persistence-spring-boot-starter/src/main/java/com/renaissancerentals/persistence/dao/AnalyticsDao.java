package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.AnalyticsEntity;
import org.springframework.data.repository.CrudRepository;

public interface AnalyticsDao extends CrudRepository<AnalyticsEntity, String> {}
