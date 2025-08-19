package com.renaissancerentals.data.domain.mapper;

import org.mapstruct.Mapper;

import com.renaissancerentals.data.domain.data.JobVacancy;
import com.renaissancerentals.data.domain.data.enumeration.EmploymentType;
import com.renaissancerentals.data.domain.data.enumeration.SalaryType;
import com.renaissancerentals.data.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.persistence.entity.JobVacancyEntity;

@Mapper(config = CentralMapperConfig.class)
public interface JobVacancyMapper {

    JobVacancy toDomain(JobVacancyEntity jobVacancyEntity);

    default EmploymentType mapEmploymentType(String value){
        return value == null ? null : EmploymentType.valueOf(value.toUpperCase());
    }

    default String mapEmploymentType(EmploymentType type){
        return type == null ? null : type.name();
    }

    default SalaryType mapSalaryType(String value){
        return value == null ? null : SalaryType.valueOf(value.toUpperCase());
    }

    default String mapSalaryType(SalaryType type){
        return type == null ? null : type.name();
    }
}
