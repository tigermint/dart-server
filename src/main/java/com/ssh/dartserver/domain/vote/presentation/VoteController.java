package com.ssh.dartserver.domain.vote.presentation;

import com.ssh.dartserver.domain.vote.dto.VoteResultRequest;
import com.ssh.dartserver.domain.vote.service.VoteService;
import com.ssh.dartserver.global.auth.service.oauth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/votes")
public class VoteController {
    private final VoteService voteService;

    @PostMapping
    public ResponseEntity<Long> create(Authentication authentication, @RequestBody @Valid VoteResultRequest request) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Long voteId = voteService.create(principal.getUser(), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(voteId)
                .toUri();
        return ResponseEntity.created(location).build();
    }
}
