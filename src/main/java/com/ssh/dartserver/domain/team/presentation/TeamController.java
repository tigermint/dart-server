package com.ssh.dartserver.domain.team.presentation;

import com.ssh.dartserver.domain.team.application.MyTeamService;
import com.ssh.dartserver.domain.team.application.TeamService;
import com.ssh.dartserver.domain.team.presentation.request.TeamSearchCondition;
import com.ssh.dartserver.domain.team.presentation.request.TeamRequest;
import com.ssh.dartserver.domain.team.presentation.response.BlindDateTeamDetailResponse;
import com.ssh.dartserver.domain.team.presentation.response.BlindDateTeamResponse;
import com.ssh.dartserver.domain.team.presentation.response.TeamResponse;
import com.ssh.dartserver.global.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/teams")
public class TeamController {

    private final TeamService teamService;
    private final MyTeamService myTeamService;

    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(Authentication authentication, @Valid @RequestBody TeamRequest request) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();

        Long teamId = Optional.of(request.getUserIds())
                .filter(List::isEmpty)
                .map(userId -> myTeamService.createSingleTeam(principal.getUser(), request))
                .orElseGet(() -> myTeamService.createMultipleTeam(principal.getUser(), request));

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(teamId)
                .toUri();
        return ResponseEntity.created(location).build();
    }

    @GetMapping("/count")
    ResponseEntity<Long> countAllTeams() {
        return ResponseEntity.ok(teamService.countAllTeam());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlindDateTeamDetailResponse> readTeam(Authentication authentication, @PathVariable("id") long id) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(teamService.readTeam(principal.getUser(), id));
    }

    // 이전 버전 클라이언트 의존성으로 인해 내부적으로 사용되지 않는 TeamSearchCondition 입력을 받는다.
    @GetMapping
    public ResponseEntity<Page<BlindDateTeamResponse>> listTeam(Authentication authentication, @Deprecated TeamSearchCondition condition, Pageable pageable) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(teamService.listVisibleTeam(principal.getUser(), pageable));
    }

}
