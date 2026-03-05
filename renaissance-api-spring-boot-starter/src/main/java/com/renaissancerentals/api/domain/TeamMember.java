package com.renaissancerentals.api.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class TeamMember {
    private Long id;
    private String name;
    private String jobTitle;
    private String email;
    private String photoLink;
    private String blogLink;
}
