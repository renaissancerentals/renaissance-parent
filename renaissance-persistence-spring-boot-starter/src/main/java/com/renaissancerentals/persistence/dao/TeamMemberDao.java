package com.renaissancerentals.persistence.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.renaissancerentals.persistence.entity.TeamMemberEntity;

public interface TeamMemberDao extends CrudRepository<TeamMemberEntity, Long> {
    List<TeamMemberEntity> findByName(String name);
}
