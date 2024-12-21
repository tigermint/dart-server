package com.ssh.dartserver.domain.proposal.application;

import com.ssh.dartserver.domain.proposal.domain.Proposal;
import com.ssh.dartserver.domain.proposal.domain.ProposalStatus;
import com.ssh.dartserver.domain.proposal.infra.ProposalRepository;
import com.ssh.dartserver.domain.team.infra.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProposalCreator {

    private final ProposalRepository proposalRepository;
    private final TeamRepository teamRepository;

    @Transactional
    public Long create(long requestingTeamId, long requestedTeamId) {
        validateAlreadySentProposal(requestingTeamId, requestedTeamId);

        Proposal proposal = Proposal.builder()
                .proposalStatus(ProposalStatus.PROPOSAL_IN_PROGRESS)
                .requestingTeam(teamRepository.getById(requestingTeamId))
                .requestedTeam(teamRepository.getById(requestedTeamId))
                .build();

        return proposalRepository.save(proposal).getId();
    }

    private void validateAlreadySentProposal(Long requestingTeamId, Long requestedTeamId) {
        // TODO 이러면은 한번이라도 보냈던 팀한테는 다시는 못보내는 거 아닌가?
        proposalRepository.findByRequestingTeamIdAndRequestedTeamId(requestingTeamId, requestedTeamId)
                .ifPresent(p -> {
                    throw new IllegalArgumentException("이미 매칭 제안 요청을 보냈습니다. RequestingTeam: " + requestingTeamId + ", RequestedTeam: " + requestedTeamId + ", 기존 ProposalId: " + p.getId() + ", Status: " + p.getProposalStatus().name());
                });
    }

}
