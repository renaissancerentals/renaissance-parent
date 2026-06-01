package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.TeamMemberEntity;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TeamMemberDao extends CrudRepository<TeamMemberEntity, Long> {
    List<TeamMemberEntity> findByName(@Param("name") String name);
}
