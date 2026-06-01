package com.renaissancerentals.api.domain.mapper;

import com.renaissancerentals.api.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.api.domain.projection.PropertyContact;
import com.renaissancerentals.api.domain.projection.PropertyLeasingOffice;
import com.renaissancerentals.persistence.entity.PropertyEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(config = CentralMapperConfig.class)
public interface PropertyMapper {

    @Mappings({@Mapping(source = "name", target = "propertyName")})
    PropertyContact toPropertyContact(PropertyEntity propertyEntity);

    PropertyLeasingOffice toPropertyLeasingOffice(PropertyEntity propertyEntity);
}
