package com.renaissancerentals.api.domain.template;

import com.renaissancerentals.api.domain.Amenity;
import com.renaissancerentals.api.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.persistence.entity.PropertyAmenityEntity;
import org.mapstruct.Mapper;

@Mapper(config = CentralMapperConfig.class)
public interface PropertyAmenityMapper {

    Amenity toDomain(PropertyAmenityEntity propertyAmenityEntity);
}
