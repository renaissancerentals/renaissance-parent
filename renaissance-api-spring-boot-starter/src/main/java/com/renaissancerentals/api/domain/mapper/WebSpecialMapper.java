package com.renaissancerentals.api.domain.mapper;

import com.renaissancerentals.api.domain.WebSpecial;
import com.renaissancerentals.api.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.persistence.entity.WebSpecialEntity;
import org.mapstruct.Mapper;

@Mapper(config = CentralMapperConfig.class)
public interface WebSpecialMapper {
    WebSpecial toDomain(WebSpecialEntity entity);
}
