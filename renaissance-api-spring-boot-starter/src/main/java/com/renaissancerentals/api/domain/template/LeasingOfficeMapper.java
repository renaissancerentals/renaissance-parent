package com.renaissancerentals.api.domain.template;

import org.mapstruct.Mapper;

import com.renaissancerentals.api.domain.LeasingOffice;
import com.renaissancerentals.api.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.persistence.entity.LeasingOfficeEntity;

@Mapper(config = CentralMapperConfig.class)
public interface LeasingOfficeMapper {

    LeasingOffice toDomain(LeasingOfficeEntity leasingOfficeEntity);
}
