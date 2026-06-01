package com.renaissancerentals.persistence.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Table;

@Data
@EqualsAndHashCode(of = "id")
@Table(name = TeamMemberEntity.TABLE_NAME)
public class TeamMemberEntity implements Serializable {
    public static final String TABLE_NAME = "team_member";

    @Id
    private Long id;

    private String name;

    private String jobTitle;

    private String email;

    private String photoLink;

    private String blogLink;

    @LastModifiedBy
    private String lastModifiedBy;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;
}
