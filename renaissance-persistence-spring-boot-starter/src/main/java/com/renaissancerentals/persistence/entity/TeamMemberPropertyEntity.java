package com.renaissancerentals.persistence.entity;

import java.io.Serializable;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Table(name = TeamMemberPropertyEntity.TABLE_NAME)
@EqualsAndHashCode(of = "id")
public class TeamMemberPropertyEntity implements Serializable {
    public static final String TABLE_NAME = "team_member_property";
    @Id
    private Long id;

    private Long teamMemberId;

    private String propertyId;

}
