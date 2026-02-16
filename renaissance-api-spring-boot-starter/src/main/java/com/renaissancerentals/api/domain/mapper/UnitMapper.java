package com.renaissancerentals.api.domain.mapper;

import org.mapstruct.Mapper;

import com.renaissancerentals.api.domain.UnitAddress;
import com.renaissancerentals.api.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.persistence.entity.UnitEntity;

@Mapper(config = CentralMapperConfig.class)
public interface UnitMapper {
    UnitAddress toAddress(UnitEntity unitEntity);

}
