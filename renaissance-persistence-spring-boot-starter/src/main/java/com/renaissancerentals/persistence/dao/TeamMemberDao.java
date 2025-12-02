package com.renaissancerentals.persistence.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.renaissancerentals.persistence.entity.TeamMemberEntity;

public interface TeamMemberDao extends CrudRepository<TeamMemberEntity, Long> {
    List<TeamMemberEntity> findByName(@Param("name") String name);
}
