package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.JobVacancyEntity;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface JobVacancyDao extends CrudRepository<JobVacancyEntity, Long> {

    List<JobVacancyEntity> findAllByActiveTrue();
}
