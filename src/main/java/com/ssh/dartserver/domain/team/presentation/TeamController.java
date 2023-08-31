package com.ssh.dartserver.domain.team.presentation;

import com.ssh.dartserver.domain.team.dto.TeamRequest;
import com.ssh.dartserver.domain.team.dto.TeamResponse;
import com.ssh.dartserver.domain.team.service.MyTeamService;
import com.ssh.dartserver.domain.team.service.TeamService;
import com.ssh.dartserver.global.auth.service.oauth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/teams")
public class TeamController {

    private final TeamService teamService;
    private final MyTeamService myTeamService;

    @PostMapping
    public ResponseEntity<TeamResponse> createTeam(Authentication authentication, @Valid @RequestBody TeamRequest request) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(myTeamService.createTeam(principal.getUser(), request));
    }

    @GetMapping("/count")
    ResponseEntity<Long> countAllTeams() {
        return ResponseEntity.ok(teamService.countAllTeams());
    }
}
