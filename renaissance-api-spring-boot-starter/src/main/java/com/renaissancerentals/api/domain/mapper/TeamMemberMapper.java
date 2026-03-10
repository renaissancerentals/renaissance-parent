package com.renaissancerentals.api.domain.mapper;

import com.renaissancerentals.api.domain.TeamMember;
import com.renaissancerentals.api.domain.mapper.config.CentralMapperConfig;
import com.renaissancerentals.persistence.entity.TeamMemberEntity;
import org.mapstruct.Mapper;

@Mapper(config = CentralMapperConfig.class)
public interface TeamMemberMapper {
    TeamMember toDomain(TeamMemberEntity teamMemberEntity);
}
