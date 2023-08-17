package com.ssh.dartserver.domain.team.presentation;

import com.ssh.dartserver.domain.team.service.TeamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/v1/teams")
public class TeamController {

    private final TeamService teamService;

    @GetMapping("/count")
    ResponseEntity<Long> countAllTeams() {
        return ResponseEntity.ok(teamService.countAllTeams());
    }
}
