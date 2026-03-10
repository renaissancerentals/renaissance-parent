package com.renaissancerentals.api.domain.mapper;

import org.mapstruct.Mapper;

import com.renaissancerentals.api.domain.HomePageSpecial;
import com.renaissancerentals.api.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.persistence.entity.HomePageSpecialEntity;

@Mapper(config = CentralMapperConfig.class)
public interface HomePageSpecialMapper {
    HomePageSpecial toDomain(HomePageSpecialEntity entity);
}
