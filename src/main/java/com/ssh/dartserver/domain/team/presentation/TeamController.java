package com.ssh.dartserver.domain.team.presentation;

import com.ssh.dartserver.domain.team.dto.BlindDateTeamDetailResponse;
import com.ssh.dartserver.domain.team.dto.BlindDateTeamResponse;
import com.ssh.dartserver.domain.team.dto.TeamRequest;
import com.ssh.dartserver.domain.team.dto.TeamResponse;
import com.ssh.dartserver.domain.team.service.MyTeamService;
import com.ssh.dartserver.domain.team.service.TeamService;
import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import com.ssh.dartserver.global.auth.service.oauth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
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
        return ResponseEntity.ok(teamService.countAllTeams());
    }

    @GetMapping
    public ResponseEntity<Page<BlindDateTeamResponse>> getTeams(Authentication authentication,
                                                                @RequestParam(value = "page", required = false, defaultValue = "0") int page,
                                                                @RequestParam(value = "size", required = false, defaultValue = "10") int size,
                                                                @RequestParam(value = "regionId", required = false, defaultValue = "0") long regionId) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        long universityId = principal.getUser().getUniversity().getId();
        Gender userGender = principal.getUser().getPersonalInfo().getGender();

        Pageable pageable = PageRequest.of(page, size);
        Page<BlindDateTeamResponse> teamResponses = teamService.listVisibleTeams(universityId, userGender, regionId, pageable);

        return ResponseEntity.ok(teamResponses);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BlindDateTeamDetailResponse> getTeam(Authentication authentication, @PathVariable("id") long id) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(teamService.readTeam(principal.getUser(), id));
    }
}
