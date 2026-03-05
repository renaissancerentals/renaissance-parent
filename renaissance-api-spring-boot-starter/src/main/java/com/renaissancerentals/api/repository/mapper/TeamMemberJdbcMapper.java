package com.renaissancerentals.api.repository.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.renaissancerentals.api.domain.TeamMember;

@Component
public class TeamMemberJdbcMapper implements RowMapper<TeamMember> {

    @Override
    public TeamMember mapRow(ResultSet rs,int rowNum) throws SQLException{
        return TeamMember.builder().id(rs.getLong("id")).name(rs.getString("name")).email(rs.getString("email"))
                .jobTitle(rs.getString("job_title")).photoLink(rs.getString("photo_link"))
                .blogLink(rs.getString("blog_link")).build();
    }
}
