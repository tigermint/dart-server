package com.ssh.dartserver.domain.team.v2;

import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.infra.TeamRepository;
import com.ssh.dartserver.domain.team.util.TeamViewCountNotificationUtil;
import com.ssh.dartserver.domain.team.v2.dto.BlindDateTeamInfo;
import com.ssh.dartserver.domain.team.v2.dto.BlindDateTeamSearchCondition;
import com.ssh.dartserver.domain.team.v2.dto.BlindDateTeamSimpleInfo;
import com.ssh.dartserver.domain.team.v2.dto.CreateTeamRequest;
import com.ssh.dartserver.domain.team.v2.dto.UpdateTeamRequest;
import com.ssh.dartserver.domain.team.v2.impl.BlindDateTeamCreator;
import com.ssh.dartserver.domain.team.v2.impl.BlindDateTeamDeleter;
import com.ssh.dartserver.domain.team.v2.impl.BlindDateTeamReader;
import com.ssh.dartserver.domain.team.v2.impl.BlindDateTeamUpdater;
import com.ssh.dartserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlindDateTeamService {
    private final BlindDateTeamCreator blindDateTeamCreator;
    private final BlindDateTeamReader blindDateTeamReader;
    private final BlindDateTeamUpdater blindDateTeamUpdater;
    private final BlindDateTeamDeleter blindDateTeamDeleter;

    private final TeamRepository teamRepository;
    private final TeamViewCountNotificationUtil teamViewCountNotificationUtil;

    // 팀 생성
    @Transactional
    public void createTeam(User user, CreateTeamRequest request) {
        log.info("새로운 팀을 생성합니다. userId: {}, request: {}", user.getId(), request);
        blindDateTeamCreator.createTeam(user, request);
    }

    // 팀 수정 (Put)
    @Transactional
    public void updateTeam(User user, long teamId, UpdateTeamRequest request) {
        log.info("팀 정보를 수정합니다. teamId: {}, request: {}", teamId, request);
        blindDateTeamUpdater.updateTeam(user, teamId, request);
    }

    // 팀 삭제
    @Transactional
    public void deleteTeam(User user, long teamId) {
        log.info("팀을 삭제합니다. teamId: {}", teamId);
        blindDateTeamDeleter.deleteTeam(user, teamId);
    }

    // 팀 목록 조회
    @Transactional
    public Page<BlindDateTeamSimpleInfo> getTeamList(User user, BlindDateTeamSearchCondition condition) {
        Page<BlindDateTeamSimpleInfo> teams = blindDateTeamReader.getTeamList(user, condition);

        // View Count Service
        List<Team> teamList = teams.stream()
                .map(team -> teamRepository.findById(team.id()))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .peek(team -> team.increaseViewCount(1))
                .toList();

        log.debug("조회된 팀. TeamIds: {}", teamList.stream().map(Team::getId).toList());
        teamViewCountNotificationUtil.postNotificationOnViewCountMileStone(teamList);

        return teams;
    }

    // 내 팀 조회
    // TODO Test 작성 필요
    @Transactional(readOnly = true)
    public BlindDateTeamInfo getUserTeamInfo(User user) {
        return blindDateTeamReader.getUserTeamInfo(user);
    }

    // 팀 상세 조회
    @Transactional
    public BlindDateTeamInfo getTeamInfo(long teamId, User user) {
        BlindDateTeamInfo teamInfo = blindDateTeamReader.getTeamInfo(teamId, user);

        // View Count Service
        Team team = teamRepository.findById(teamInfo.id()).orElseThrow();
        team.increaseViewCount(1);

        log.debug("조회된 팀. TeamId: {}, Now ViewCount: {}", teamInfo.id(), team.getViewCount());
        teamViewCountNotificationUtil.postNotificationOnViewCountMileStone(team);

        return teamInfo;
    }

}
