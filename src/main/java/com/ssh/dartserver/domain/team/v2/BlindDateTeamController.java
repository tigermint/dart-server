package com.ssh.dartserver.domain.team.v2;

import com.ssh.dartserver.domain.team.v2.dto.BlindDateTeamInfo;
import com.ssh.dartserver.domain.team.v2.dto.BlindDateTeamSimpleInfo;
import com.ssh.dartserver.domain.team.v2.dto.CreateTeamRequest;
import com.ssh.dartserver.domain.team.v2.dto.UpdateTeamRequest;
import com.ssh.dartserver.global.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v2/teams")
public class BlindDateTeamController {
    private final BlindDateTeamService blindTeamService;

    // 팀 생성
    @PostMapping
    public void createTeam(Authentication authentication, @RequestBody CreateTeamRequest request) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        blindTeamService.createTeam( principal.getUser(), request);
    }

    // 팀 수정
    @PutMapping("/{id}")
    public void updateTeam(Authentication authentication, @PathVariable long id, @RequestBody UpdateTeamRequest request) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        blindTeamService.updateTeam(principal.getUser(), id, request);
    }

    // 팀 삭제
    @DeleteMapping("/{id}")
    public void deleteTeam(Authentication authentication, @PathVariable long id) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        blindTeamService.deleteTeam(principal.getUser(), id);
    }

    // 팀 목록 조회
    @GetMapping
    public Page<BlindDateTeamSimpleInfo> getTeams(Authentication authentication, Pageable pageable) {  // TODO pageable을 대체할 condition 객체 만들기
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return blindTeamService.getTeamList(principal.getUser(), pageable);
    }

    // 팀 상세 조회
    @GetMapping("/{id}")
    public BlindDateTeamInfo getTeam(Authentication authentication, @PathVariable long id) {
        return blindTeamService.getTeamInfo(id);
    }

    // 내 팀 조회
    @GetMapping("/my")
    public BlindDateTeamInfo getMyTeam(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return blindTeamService.getUserTeamInfo(principal.getUser());
    }

}
