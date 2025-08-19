package com.renaissancerentals.data.persistence.dao;

import com.renaissancerentals.data.persistence.entity.JobVacancyEntity;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JobVacancyDao extends CrudRepository<JobVacancyEntity, Long> {

    List<JobVacancyEntity> findAllByActiveTrue();

}
