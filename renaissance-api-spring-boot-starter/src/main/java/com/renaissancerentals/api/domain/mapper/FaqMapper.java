package com.renaissancerentals.api.domain.mapper;

import org.mapstruct.Mapper;

import com.renaissancerentals.api.domain.Faq;
import com.renaissancerentals.api.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.persistence.entity.PropertyFaqEntity;

@Mapper(config = CentralMapperConfig.class)
public interface FaqMapper {
    Faq toFaq(PropertyFaqEntity propertyFaqEntity);

}
