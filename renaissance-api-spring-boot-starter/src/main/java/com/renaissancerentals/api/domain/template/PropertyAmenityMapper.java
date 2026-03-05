package com.renaissancerentals.api.domain.template;

import org.mapstruct.Mapper;

import com.renaissancerentals.api.domain.Amenity;
import com.renaissancerentals.api.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.persistence.entity.PropertyAmenityEntity;

@Mapper(config = CentralMapperConfig.class)
public interface PropertyAmenityMapper {

    Amenity toDomain(PropertyAmenityEntity propertyAmenityEntity);
}
