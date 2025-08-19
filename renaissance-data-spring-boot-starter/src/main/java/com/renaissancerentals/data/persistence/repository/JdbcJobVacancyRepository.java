package com.renaissancerentals.data.persistence.repository;

import com.renaissancerentals.data.domain.data.JobVacancy;
import com.renaissancerentals.data.domain.mapper.JobVacancyMapper;
import com.renaissancerentals.data.domain.repository.JobVacancyRepository;
import com.renaissancerentals.data.persistence.dao.JobVacancyDao;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Repository
public class JdbcJobVacancyRepository implements JobVacancyRepository {
    private final JobVacancyDao jobVacancyDao;
    private final JobVacancyMapper jobVacancyMapper;

    @Override
    public Optional<JobVacancy> getJobVacancy(Long id) {
        return jobVacancyDao.findById(id).map(jobVacancyMapper::toDomain);
    }

    @Override
    public List<JobVacancy> getActiveJobVacancies() {
        return jobVacancyDao.findAllByActiveTrue().stream().map(jobVacancyMapper::toDomain).toList();
    }
}
