package com.ssh.dartserver.domain.proposal.application;

import com.ssh.dartserver.domain.proposal.domain.Proposal;
import com.ssh.dartserver.domain.proposal.domain.ProposalStatus;
import com.ssh.dartserver.domain.proposal.infra.ProposalRepository;
import com.ssh.dartserver.domain.proposal.presentation.request.ProposalRequest;
import com.ssh.dartserver.domain.proposal.presentation.response.ProposalResponse;
import com.ssh.dartserver.domain.team.domain.SingleTeamFriend;
import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamRegion;
import com.ssh.dartserver.domain.team.domain.TeamUser;
import com.ssh.dartserver.domain.team.infra.TeamRepository;
import com.ssh.dartserver.domain.team.infra.TeamUserRepository;
import com.ssh.dartserver.domain.team.v2.dto.BlindDateTeamInfo;
import com.ssh.dartserver.domain.team.v2.impl.BlindDateTeamReader;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.personalinfo.BirthYear;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.global.infra.notification.PlatformNotification;
import com.ssh.dartserver.global.util.TeamAverageAgeCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProposalService {
    private static final int PROPOSAL_POINT = 0;
    private static final String PROPOSAL_CONTENTS = "매칭 제안이 도착했어요 💌";

    private final ProposalRepository proposalRepository;
    private final ProposalCreator proposalCreator;
    private final ProposalReader proposalReader;
    private final BlindDateTeamReader blindDateTeamReader;

    private final ProposalMapper proposalMapper;

    private final PlatformNotification notification;


    @Transactional
    public Long createProposal(User user, ProposalRequest.Create request) {
        BlindDateTeamInfo requestedTeam = blindDateTeamReader.getTeamInfo(request.getRequestedTeamId(), user);
        BlindDateTeamInfo requestingTeam = blindDateTeamReader.getTeamInfo(request.getRequestingTeamId(), user);
        if (requestingTeam.leaderId() != user.getId()) {
            throw new IllegalArgumentException("유저가 해당 팀에 속해있지 않습니다. (userId:" + user.getId() + ")");
        }

        user.subtractPoint(PROPOSAL_POINT);

        Long proposalId = proposalCreator.create(requestingTeam.id(), requestedTeam.id());

        CompletableFuture.runAsync(() ->
                notification.postNotificationSpecificDevice(requestedTeam.leaderId(), PROPOSAL_CONTENTS)
        );

        log.info("호감을 전송합니다. ProposalId: {} (팀 {} -> 팀 {})", proposalId, request.getRequestingTeamId(), request.getRequestedTeamId());
        return proposalId;
    }

    public List<ProposalResponse.ListDto> listSentProposal(User user) {
        return proposalReader.listSentProposal(user);
    }

    public List<ProposalResponse.ListDto> listReceivedProposal(User user) {
        return proposalReader.listReceivedProposal(user);
    }

    @Transactional
    public ProposalResponse.UpdateDto updateProposal(User user, Long proposalId, ProposalRequest.Update request) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new IllegalArgumentException("매칭 제안이 존재하지 않습니다."));
        //TODO: 유저가 requestedTeam에 속해있는지 확인
        proposal.updateProposalStatus(ProposalStatus.valueOf(request.getProposalStatus()));
        return proposalMapper.toUpdateDto(proposal);
    }

}

