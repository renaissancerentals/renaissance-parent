package com.renaissancerentals.api.domain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

import com.renaissancerentals.api.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.api.messaging.ContactAdditionalInfoRequest;
import com.renaissancerentals.api.messaging.ContactMessageRequest;
import com.renaissancerentals.persistence.entity.ContactEmailEntity;

@Mapper(config = CentralMapperConfig.class)
public interface ContactMessageMapper {

    @Mappings({@Mapping(source = "email", target = "fromEmail"), @Mapping(source = "phone", target = "fromPhone"),
            @Mapping(source = "currentPage", target = "sourceUrl"),
            @Mapping(source = "question", target = "rawQuestion"),
            @Mapping(source = "additionalInfo", target = "additionalInfo")})
    ContactEmailEntity toEntity(ContactMessageRequest contactMessage);

    default ContactAdditionalInfoRequest map(ContactAdditionalInfoRequest info){
        return info;
    }

}
