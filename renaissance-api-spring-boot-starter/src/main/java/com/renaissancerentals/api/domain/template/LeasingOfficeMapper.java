package com.renaissancerentals.api.domain.template;

import com.renaissancerentals.api.domain.LeasingOffice;
import com.renaissancerentals.api.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.persistence.entity.LeasingOfficeEntity;
import org.mapstruct.Mapper;

@Mapper(config = CentralMapperConfig.class)
public interface LeasingOfficeMapper {

    LeasingOffice toDomain(LeasingOfficeEntity leasingOfficeEntity);
}
