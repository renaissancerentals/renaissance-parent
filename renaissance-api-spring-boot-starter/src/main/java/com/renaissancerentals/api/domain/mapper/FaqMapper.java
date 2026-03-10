package com.renaissancerentals.api.domain.mapper;

import com.renaissancerentals.api.domain.Faq;
import com.renaissancerentals.api.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.persistence.entity.FloorplanFaqEntity;
import com.renaissancerentals.persistence.entity.MaintenanceFaqEntity;
import com.renaissancerentals.persistence.entity.PropertyFaqEntity;
import com.renaissancerentals.persistence.entity.ResidentFaqEntity;
import org.mapstruct.Mapper;

@Mapper(config = CentralMapperConfig.class)
public interface FaqMapper {
    Faq toFaq(PropertyFaqEntity propertyFaqEntity);

    Faq toFaq(FloorplanFaqEntity floorplanFaqEntity);

    Faq toFaq(ResidentFaqEntity residentFaqEntity);

    Faq toFaq(MaintenanceFaqEntity maintenanceFaqEntity);
}
