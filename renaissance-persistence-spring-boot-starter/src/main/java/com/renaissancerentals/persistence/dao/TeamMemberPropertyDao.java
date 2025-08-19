package com.renaissancerentals.persistence.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.TeamMemberPropertyEntity;

public interface TeamMemberPropertyDao extends CrudRepository<TeamMemberPropertyEntity, Long> {

    List<TeamMemberPropertyEntity> findByTeamMemberId(Long teamMemberId);
}
