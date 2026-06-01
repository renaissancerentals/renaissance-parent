package com.renaissancerentals.api.domain.template;

import com.renaissancerentals.api.domain.PropertyBusRoute;
import com.renaissancerentals.api.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.persistence.entity.PropertyBusRouteEntity;
import org.mapstruct.Mapper;

@Mapper(config = CentralMapperConfig.class)
public interface PropertyBusRouteMapper {

    PropertyBusRoute toDomain(PropertyBusRouteEntity propertyBusRouteEntity);
}
