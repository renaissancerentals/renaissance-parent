package com.renaissancerentals.api.repository.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.renaissancerentals.api.domain.TeamMemberDetails;

@Component
public class TeamMemberDetailsMapper implements RowMapper<TeamMemberDetails> {

    @Override
    public TeamMemberDetails mapRow(ResultSet rs,int rowNum) throws SQLException{
        return TeamMemberDetails.builder().name(rs.getString("name")).email(rs.getString("email")).build();
    }
}
