package com.renaissancerentals.api.repository;

import java.util.Optional;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.renaissancerentals.api.domain.TeamMemberDetails;
import com.renaissancerentals.api.repository.helper.SqlBuilder;
import com.renaissancerentals.api.repository.mapper.TeamMemberDetailsMapper;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class PropertyRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final TeamMemberDetailsMapper teamMemberDetailsMapper;

    private static final String SQL = """
            SELECT DISTINCT
                tm.id        AS teamMemberId,
                tm.name      AS name,
                tm.job_title AS jobTitle,
                tm.email     AS email
            FROM team_member tm
                     JOIN team_member_property tmp
                          ON tmp.team_member_id = tm.id
            """;

    public Optional<TeamMemberDetails> getPropertyManager(String propertyId){
        SqlBuilder sqlBuilder = new SqlBuilder(SQL).where("tm.job_title = :jobTitle","jobTitle","Brand Manager")
                .where("tmp.property_id = :propertyId","propertyId",propertyId);
        return jdbcTemplate.query(sqlBuilder.sql(),sqlBuilder.params(),teamMemberDetailsMapper).stream().findFirst();

    }
}
