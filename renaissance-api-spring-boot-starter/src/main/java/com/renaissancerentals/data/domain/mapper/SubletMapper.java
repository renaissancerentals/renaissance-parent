package com.renaissancerentals.data.domain.mapper;

import org.mapstruct.Mapper;

import com.renaissancerentals.data.domain.data.Sublet;
import com.renaissancerentals.data.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.persistence.entity.SubletEntity;

@Mapper(config = CentralMapperConfig.class)
public interface SubletMapper {

    Sublet toDomain(SubletEntity subletEntity);
}
