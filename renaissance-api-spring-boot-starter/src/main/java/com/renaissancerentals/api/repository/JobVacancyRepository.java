package com.renaissancerentals.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.renaissancerentals.api.domain.JobVacancy;
import com.renaissancerentals.api.domain.mapper.JobVacancyMapper;
import com.renaissancerentals.persistence.dao.JobVacancyDao;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class JobVacancyRepository {
    private final JobVacancyDao jobVacancyDao;
    private final JobVacancyMapper jobVacancyMapper;

    public Optional<JobVacancy> getJobVacancy(Long id){
        return jobVacancyDao.findById(id).map(jobVacancyMapper::toDomain);
    }

    public List<JobVacancy> getActiveJobVacancies(){
        return jobVacancyDao.findAllByActiveTrue().stream().map(jobVacancyMapper::toDomain).toList();
    }
}
