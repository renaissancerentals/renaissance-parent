package com.renaissancerentals.persistence.dao;

import com.renaissancerentals.persistence.entity.TeamMemberPropertyEntity;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface TeamMemberPropertyDao extends CrudRepository<TeamMemberPropertyEntity, Long> {

    List<TeamMemberPropertyEntity> findByTeamMemberId(@Param("teamMemberId") Long teamMemberId);
}
