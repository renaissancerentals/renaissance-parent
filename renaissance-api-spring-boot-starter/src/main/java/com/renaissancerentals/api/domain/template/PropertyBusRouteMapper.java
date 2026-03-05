package com.renaissancerentals.api.domain.template;

import org.mapstruct.Mapper;

import com.renaissancerentals.api.domain.PropertyBusRoute;
import com.renaissancerentals.api.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.persistence.entity.PropertyBusRouteEntity;

@Mapper(config = CentralMapperConfig.class)
public interface PropertyBusRouteMapper {

    PropertyBusRoute toDomain(PropertyBusRouteEntity propertyBusRouteEntity);
}
