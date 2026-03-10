package com.renaissancerentals.api.domain.mapper;

import org.mapstruct.Mapper;

import com.renaissancerentals.api.domain.Utility;
import com.renaissancerentals.api.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.persistence.entity.UtilityEntity;

@Mapper(config = CentralMapperConfig.class)
public interface UtilityMapper {

    Utility toDomain(UtilityEntity utilityEntity);
}
