package com.renaissancerentals.api.domain.mapper;

import com.renaissancerentals.api.domain.HomePageLinkedImageRegion;
import com.renaissancerentals.api.domain.HomePageSpecial;
import com.renaissancerentals.api.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.persistence.entity.HomePageSpecialEntity;
import com.renaissancerentals.persistence.entity.LinkedImageRegion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = CentralMapperConfig.class)
public interface HomePageSpecialMapper {
    @Mapping(target = "links", source = "links.regions")
    HomePageSpecial toDomain(HomePageSpecialEntity entity);

    HomePageLinkedImageRegion toDomain(LinkedImageRegion region);
}
