package com.ssh.dartserver.domain.team.service;

import com.ssh.dartserver.domain.team.infra.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {
    private final TeamRepository teamRepository;
    public Long countAllTeams() {
        return teamRepository.count() * 2 + 50;
    }
}
