package com.renaissancerentals.persistence.entity;

import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = TeamMemberPropertyEntity.TABLE_NAME)
@EqualsAndHashCode(of = "id")
public class TeamMemberPropertyEntity implements Serializable, PropertyAware {
    public static final String TABLE_NAME = "team_member_property";

    @Id
    private Long id;

    private Long teamMemberId;

    private String propertyId;
}
