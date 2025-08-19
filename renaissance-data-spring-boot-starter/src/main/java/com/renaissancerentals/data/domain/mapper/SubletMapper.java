package com.renaissancerentals.data.domain.mapper;

import com.renaissancerentals.data.domain.data.Sublet;
import com.renaissancerentals.data.persistence.entity.SubletEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.NullValueMappingStrategy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        nullValueMappingStrategy = NullValueMappingStrategy.RETURN_DEFAULT)
public interface SubletMapper {

    SubletEntity toEntity(Sublet sublet);

    Sublet toDomain(SubletEntity subletEntity);
}
