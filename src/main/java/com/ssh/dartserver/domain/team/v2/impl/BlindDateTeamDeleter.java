package com.ssh.dartserver.domain.team.v2.impl;

import com.ssh.dartserver.domain.team.application.MyTeamService;
import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.infra.TeamRepository;
import com.ssh.dartserver.domain.user.domain.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlindDateTeamDeleter {

    private final TeamRepository teamRepository;
    private final MyTeamService myTeamService;

    // 팀 삭제
    @Transactional
    public void deleteTeam(User user, long teamId) {
        // 검증
        if (user == null) {
            throw new IllegalArgumentException("사용자 정보는 null일 수 없습니다.");
        }

        Optional<Team> team = teamRepository.findById(teamId);
        if (team.isEmpty()) {
            return;  // 이미 삭제된 경우, 무시
        }

        if (team.get().getTeamUsersCombinationHash() != null) {
            // v1 처리
            log.debug("v1 팀을 삭제합니다.");
            myTeamService.deleteTeam(user, teamId);
            return;
        }

        // v2 처리
        log.debug("v2 팀을 삭제합니다.");
        if (!team.get().isLeader(user)) {
            throw new IllegalArgumentException(
                    "자신이 만든 팀만 삭제할 수 있습니다. team.leaderId: " + team.get().getLeader().getId());
        }

        // 로직
        teamRepository.delete(team.get());  // 연관 객체 처리는 어케되있는지 확인!
    }

}
