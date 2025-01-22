package com.ssh.dartserver.domain.proposal.presentation;

import com.ssh.dartserver.domain.proposal.application.ProposalService;
import com.ssh.dartserver.domain.proposal.presentation.request.ProposalRequest;
import com.ssh.dartserver.domain.proposal.presentation.response.ProposalResponse;
import com.ssh.dartserver.global.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/users")
public class MyProposalController {

    private final ProposalService proposalService;

    @GetMapping("/me/proposals")
    public ResponseEntity<List<ProposalResponse.ListDto>> listProposal(Authentication authentication, @RequestParam(defaultValue = "sent") String type) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        if (type.equals("sent")) {  // TODO Enum
            return ResponseEntity.ok(proposalService.listSentProposal(principal.getUser()));
        }
        if (type.equals("received")) {
            return ResponseEntity.ok(proposalService.listReceivedProposal(principal.getUser()));
        }
        return ResponseEntity.badRequest().build();
    }

    @PatchMapping("/me/proposals/{proposalId}")
    public ResponseEntity<ProposalResponse.UpdateDto> updateProposal(Authentication authentication, @PathVariable Long proposalId, @RequestBody ProposalRequest.Update request) {
        PrincipalDetails principal = (PrincipalDetails) authentication.getPrincipal();
        return ResponseEntity.ok(proposalService.updateProposal(principal.getUser(), proposalId, request));
    }

}
