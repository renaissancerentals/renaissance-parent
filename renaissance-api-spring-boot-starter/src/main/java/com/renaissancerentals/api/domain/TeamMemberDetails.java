package com.renaissancerentals.api.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TeamMemberDetails {

    private String name;

    private String email;
}
