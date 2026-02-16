package com.renaissancerentals.api.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.renaissancerentals.api.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.api.messaging.ApplicationRequest;
import com.renaissancerentals.persistence.entity.ApplicationEmailEntity;

@Mapper(config = CentralMapperConfig.class)
public interface ApplicationRequestMapper {

    @Mappings({@Mapping(source = "email", target = "fromEmail"), @Mapping(source = "phone", target = "fromPhone"),
            @Mapping(source = "currentPage", target = "sourceUrl"),
            @Mapping(source = "questions", target = "rawQuestions"),
            @Mapping(source = "community", target = "interestedCommunity"),
            @Mapping(source = "address", target = "interestedLocation")})
    ApplicationEmailEntity toEntity(ApplicationRequest applicationRequest);

}
