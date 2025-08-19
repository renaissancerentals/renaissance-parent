package com.renaissancerentals.data.domain.repository;

import com.renaissancerentals.data.domain.data.JobVacancy;

import java.util.List;
import java.util.Optional;

public interface JobVacancyRepository {
    Optional<JobVacancy> getJobVacancy(Long id);

    List<JobVacancy> getActiveJobVacancies();
}
