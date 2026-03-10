package com.renaissancerentals.api.domain.mapper;

import org.mapstruct.Mapper;

import com.renaissancerentals.api.domain.Amenity;
import com.renaissancerentals.api.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.persistence.entity.AmenityEntity;

@Mapper(config = CentralMapperConfig.class)
public interface AmenityMapper {

    Amenity toDomain(AmenityEntity amenityEntity);
}
