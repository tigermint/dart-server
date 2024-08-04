package com.ssh.dartserver.domain.proposal.presentation;

import com.ssh.dartserver.domain.proposal.presentation.request.ProposalRequest;
import com.ssh.dartserver.domain.proposal.application.ProposalService;
import com.ssh.dartserver.global.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/proposals")
public class ProposalController {
    private final ProposalService proposalService;
    @PostMapping
    public ResponseEntity<Void> createProposal(Authentication authentication, @RequestBody ProposalRequest.Create request) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        Long proposalId = proposalService.createProposal(principal.getUser(), request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(proposalId)
                .toUri();
        return ResponseEntity.created(location).build();
    }
}
