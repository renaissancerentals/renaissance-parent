package com.renaissancerentals.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.renaissancerentals.api.domain.JobVacancy;
import com.renaissancerentals.api.error.NotFoundException;
import com.renaissancerentals.api.repository.JobVacancyRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/job-vacancies")
@RequiredArgsConstructor
public class JobVacancyController {

    private final JobVacancyRepository jobVacancyRepository;

    @GetMapping("/{jobVacancyId}")
    public ResponseEntity<JobVacancy> getJobVacancy(@PathVariable("jobVacancyId") Long jobVacancyId){
        return ResponseEntity.ok(jobVacancyRepository.getJobVacancy(jobVacancyId)
                .orElseThrow(() -> new NotFoundException(String.format("Job Vacancy with id: %d",jobVacancyId))));
    }

    @GetMapping()
    public ResponseEntity<List<JobVacancy>> getActiveJobVacancies(){
        return ResponseEntity.ok(jobVacancyRepository.getActiveJobVacancies());
    }

}
