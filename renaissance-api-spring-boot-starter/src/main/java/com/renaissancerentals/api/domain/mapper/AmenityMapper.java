package com.renaissancerentals.api.domain.mapper;

import com.renaissancerentals.api.domain.Amenity;
import com.renaissancerentals.api.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.persistence.entity.AmenityEntity;
import org.mapstruct.Mapper;

@Mapper(config = CentralMapperConfig.class)
public interface AmenityMapper {

    Amenity toDomain(AmenityEntity amenityEntity);
}
