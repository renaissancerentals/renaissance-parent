package com.renaissancerentals.data.domain.mapper;

import com.renaissancerentals.data.domain.data.JobVacancy;
import com.renaissancerentals.data.domain.data.enumeration.EmploymentType;
import com.renaissancerentals.data.domain.data.enumeration.SalaryType;
import com.renaissancerentals.data.persistence.entity.JobVacancyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface JobVacancyMapper {

    JobVacancyEntity toEntity(JobVacancy jobVacancy);

    JobVacancy toDomain(JobVacancyEntity jobVacancyEntity);

    default EmploymentType mapEmploymentType(String value) {
        return value == null ? null : EmploymentType.valueOf(value.toUpperCase());
    }

    default String mapEmploymentType(EmploymentType type) {
        return type == null ? null : type.name();
    }

    default SalaryType mapSalaryType(String value) {
        return value == null ? null : SalaryType.valueOf(value.toUpperCase());
    }

    default String mapSalaryType(SalaryType type) {
        return type == null ? null : type.name();
    }
}
