package com.ssh.dartserver.domain.team.v2;

import com.ssh.dartserver.domain.image.application.ImageUploader;
import com.ssh.dartserver.domain.team.infra.TeamRepository;
import com.ssh.dartserver.domain.team.v2.dto.CreateTeamRequest;
import com.ssh.dartserver.domain.team.v2.dto.UpdateTeamRequest;
import com.ssh.dartserver.domain.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BlindDateTeamService {
    private final TeamRepository teamRepository;
    private final TeamImageRepository teamImageRepository;
    private final ImageUploader imageUploader;

    // 팀 생성
    public void createTeam(User user, CreateTeamRequest request) {
        throw new UnsupportedOperationException();
    }

    // 팀 수정
    public void updateTeam(User user, UpdateTeamRequest request) {
        throw new UnsupportedOperationException();
    }

    // 팀 삭제
    public void deleteTeam(User user, long teamId) {
        throw new UnsupportedOperationException();
    }

    // 팀 목록 조회
    public void getTeamList(Pageable pageable) {
        throw new UnsupportedOperationException();
    }

    // 팀 상세 조회
    public void getTeamInfo(long teamId) {
        throw new UnsupportedOperationException();
    }

}
