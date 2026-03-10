package com.renaissancerentals.api.service;

import java.util.List;
import java.util.stream.StreamSupport;

import org.springframework.stereotype.Service;

import com.renaissancerentals.api.domain.TeamMember;
import com.renaissancerentals.api.domain.mapper.TeamMemberMapper;
import com.renaissancerentals.persistence.dao.TeamMemberDao;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeamMemberService {

    private final TeamMemberDao teamMemberDao;
    private final TeamMemberMapper teamMemberMapper;

    public List<TeamMember> getAllTeamMembers(){
        return StreamSupport.stream(teamMemberDao.findAll().spliterator(),false).map(teamMemberMapper::toDomain)
                .toList();
    }
}
