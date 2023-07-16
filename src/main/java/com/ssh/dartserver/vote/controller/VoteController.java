package com.ssh.dartserver.vote.controller;

import com.ssh.dartserver.auth.service.oauth.PrincipalDetails;
import com.ssh.dartserver.vote.dto.VoteResultRequest;
import com.ssh.dartserver.vote.dto.ReceivedVoteResponse;
import com.ssh.dartserver.vote.service.VoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/votes")
public class VoteController {
    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<String> create(Authentication authentication, @RequestBody @Valid VoteResultRequest request) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        voteService.create(principal.getUser(), request);
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/{voteId}")
    public ResponseEntity<ReceivedVoteResponse> read(@PathVariable Long voteId) {
        return ResponseEntity.ok(voteService.read(voteId));
    }

    @GetMapping
    public ResponseEntity<List<ReceivedVoteResponse>> list(Authentication authentication) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(voteService.list(principal.getUser()));
    }

}
