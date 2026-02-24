package com.renaissancerentals.api.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.renaissancerentals.api.domain.PropertyContact;
import com.renaissancerentals.api.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.persistence.entity.PropertyEntity;

@Mapper(config = CentralMapperConfig.class)
public interface PropertyMapper {

    @Mappings({@Mapping(source = "name", target = "propertyName")})
    PropertyContact toPropertyContact(PropertyEntity propertyEntity);

}
