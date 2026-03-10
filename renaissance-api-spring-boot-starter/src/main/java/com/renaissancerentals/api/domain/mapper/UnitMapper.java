package com.renaissancerentals.api.domain.mapper;

import com.renaissancerentals.api.domain.Unit;
import com.renaissancerentals.api.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.api.domain.projection.UnitAddress;
import com.renaissancerentals.persistence.entity.UnitEntity;
import org.mapstruct.Mapper;

@Mapper(config = CentralMapperConfig.class)
public interface UnitMapper {
    UnitAddress toAddress(UnitEntity unitEntity);

    Unit toDomain(UnitEntity unitEntity);
}
