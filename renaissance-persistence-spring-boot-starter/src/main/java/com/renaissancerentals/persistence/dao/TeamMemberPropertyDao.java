package com.renaissancerentals.persistence.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.renaissancerentals.persistence.entity.TeamMemberPropertyEntity;

public interface TeamMemberPropertyDao extends CrudRepository<TeamMemberPropertyEntity, Long> {

    List<TeamMemberPropertyEntity> findByTeamMemberId(@Param("teamMemberId") Long teamMemberId);
}
