package com.renaissancerentals.api.domain.mapper;

import org.mapstruct.Mapper;

import com.renaissancerentals.api.domain.WebSpecial;
import com.renaissancerentals.api.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.persistence.entity.WebSpecialEntity;

@Mapper(config = CentralMapperConfig.class)
public interface WebSpecialMapper {
    WebSpecial toDomain(WebSpecialEntity entity);
}
