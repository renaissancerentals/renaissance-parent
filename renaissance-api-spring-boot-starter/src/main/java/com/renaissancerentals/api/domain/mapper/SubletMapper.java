package com.renaissancerentals.api.domain.mapper;

import org.mapstruct.Mapper;

import com.renaissancerentals.api.domain.Sublet;
import com.renaissancerentals.api.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.api.messaging.SubletRequest;
import com.renaissancerentals.persistence.entity.SubletEntity;

@Mapper(config = CentralMapperConfig.class)
public interface SubletMapper {
    Sublet fromEntity(SubletEntity subletEntity);

    SubletEntity toEntity(SubletRequest sublet);

}
