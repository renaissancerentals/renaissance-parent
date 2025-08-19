package com.renaissancerentals.persistence.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.JobVacancyEntity;

public interface JobVacancyDao extends CrudRepository<JobVacancyEntity, Long> {

    List<JobVacancyEntity> findAllByActiveTrue();

}
