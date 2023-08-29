package com.ssh.dartserver.domain.vote.presentation;

import com.ssh.dartserver.domain.vote.dto.ReceivedVoteResponse;
import com.ssh.dartserver.domain.vote.dto.VoteResultRequest;
import com.ssh.dartserver.domain.vote.service.VoteService;
import com.ssh.dartserver.global.auth.service.oauth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/votes")
public class VoteController {
    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<ReceivedVoteResponse> create(Authentication authentication, @RequestBody @Valid VoteResultRequest request) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(voteService.create(principal.getUser(), request));
    }
}
