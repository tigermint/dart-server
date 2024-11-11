package com.ssh.dartserver.domain.team.v2;

import com.ssh.dartserver.domain.team.v2.dto.BlindDateTeamInfo;
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
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BlindDateTeamService {
    private final BlindDateTeamCreator blindDateTeamCreator;
    private final BlindDateTeamReader blindDateTeamReader;
    private final BlindDateTeamUpdater blindDateTeamUpdater;
    private final BlindDateTeamDeleter blindDateTeamDeleter;

    // 팀 생성
    @Transactional
    public void createTeam(User user, CreateTeamRequest request) {
        blindDateTeamCreator.createTeam(user, request);
    }

    // 팀 수정 (Put)
    @Transactional
    public void updateTeam(User user, long teamId, UpdateTeamRequest request) {
        blindDateTeamUpdater.updateTeam(user, teamId, request);
    }

    // 팀 삭제
    @Transactional
    public void deleteTeam(User user, long teamId) {
        blindDateTeamDeleter.deleteTeam(user, teamId);
    }

    // 팀 목록 조회
    @Transactional(readOnly = true)
    public Page<BlindDateTeamSimpleInfo> getTeamList(User user, Pageable pageable) {
        return blindDateTeamReader.getTeamList(user, pageable);
    }

    // 내 팀 조회
    // TODO Test 작성 필요
    @Transactional(readOnly = true)
    public BlindDateTeamInfo getUserTeamInfo(User user) {
        return blindDateTeamReader.getUserTeamInfo(user);
    }

    // 팀 상세 조회
    @Transactional(readOnly = true)
    public BlindDateTeamInfo getTeamInfo(long teamId) {
        return blindDateTeamReader.getTeamInfo(teamId);
    }

}
