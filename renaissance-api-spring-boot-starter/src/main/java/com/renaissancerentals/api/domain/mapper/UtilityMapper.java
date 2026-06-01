package com.renaissancerentals.api.domain.mapper;

import com.renaissancerentals.api.domain.Utility;
import com.renaissancerentals.api.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.persistence.entity.UtilityEntity;
import org.mapstruct.Mapper;

@Mapper(config = CentralMapperConfig.class)
public interface UtilityMapper {

    Utility toDomain(UtilityEntity utilityEntity);
}
