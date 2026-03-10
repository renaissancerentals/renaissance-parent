package com.renaissancerentals.api.domain.mapper;

import org.mapstruct.Mapper;

import com.renaissancerentals.api.domain.TeamMember;
import com.renaissancerentals.api.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.persistence.entity.TeamMemberEntity;

@Mapper(config = CentralMapperConfig.class)
public interface TeamMemberMapper {
    TeamMember toDomain(TeamMemberEntity teamMemberEntity);
}
