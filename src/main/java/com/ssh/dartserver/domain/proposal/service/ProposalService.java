package com.ssh.dartserver.domain.proposal.service;

import com.ssh.dartserver.domain.proposal.domain.Proposal;
import com.ssh.dartserver.domain.proposal.domain.ProposalStatus;
import com.ssh.dartserver.domain.proposal.dto.ProposalRequest;
import com.ssh.dartserver.domain.proposal.dto.ProposalResponse;
import com.ssh.dartserver.domain.proposal.dto.mapper.ProposalMapper;
import com.ssh.dartserver.domain.proposal.infra.ProposalRepository;
import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamRegion;
import com.ssh.dartserver.domain.team.domain.TeamUser;
import com.ssh.dartserver.domain.team.infra.TeamRegionRepository;
import com.ssh.dartserver.domain.team.infra.TeamUserRepository;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.global.utils.DateTimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProposalService {
    private static final int PROPOSAL_POINT = 200;

    private final ProposalRepository proposalRepository;
    private final TeamUserRepository teamUserRepository;
    private final TeamRegionRepository teamRegionRepository;
    private final UserRepository userRepository;

    private final ProposalMapper proposalMapper;

    @Transactional
    public Long createProposal(User user, ProposalRequest.Create request) {
        validateAlreadySentProposal(request.getRequestingTeamId(), request.getRequestedTeamId());

        List<TeamUser> requestingTeamUsers = teamUserRepository.findAllByTeamId(request.getRequestingTeamId());
        List<TeamUser> requestedTeamUsers = teamUserRepository.findAllByTeamId(request.getRequestedTeamId());
        validateUserInTeam(user, requestingTeamUsers);

        user.subtractPoint(PROPOSAL_POINT);

        Proposal proposal = Proposal.builder()
                .proposalStatus(ProposalStatus.PROPOSAL_IN_PROGRESS)
                .requestingTeam(requestingTeamUsers.get(0).getTeam())
                .requestedTeam(requestedTeamUsers.get(0).getTeam())
                .build();

        userRepository.save(user);
        proposalRepository.save(proposal);

        return proposal.getId();
    }


    public List<ProposalResponse.ListDto> listSentProposal(User user) {
        String userIdPattern = "%-" + user.getId() + "-%";
        return getListDtos(proposalRepository.findAllRequestingTeamByUserIdPatternAndProposalStatus(userIdPattern, ProposalStatus.PROPOSAL_IN_PROGRESS));
    }

    public List<ProposalResponse.ListDto> listReceivedProposal(User user) {
        String userIdPattern = "%-" + user.getId() + "-%";
        return getListDtos(proposalRepository.findAllRequestedTeamByUserIdPatternAndProposalStatus(userIdPattern, ProposalStatus.PROPOSAL_IN_PROGRESS));
    }

    @Transactional
    public ProposalResponse.UpdateDto updateProposal(User user, Long proposalId, ProposalRequest.Update request) {
        Proposal proposal = proposalRepository.findById(proposalId)
                .orElseThrow(() -> new IllegalArgumentException("매칭 제안이 존재하지 않습니다."));
        //TODO: 유저가 requestedTeam에 속해있는지 확인
        proposal.updateProposalStatus(ProposalStatus.valueOf(request.getProposalStatus()));
        return proposalMapper.toUpdateDto(proposal);
    }


    private List<ProposalResponse.ListDto> getListDtos(List<Proposal> proposals) {
        return proposals.stream()
                .map(proposal -> {
                    //팀 가져오기
                    Team requestingTeam = proposal.getRequestingTeam();
                    Team requestedTeam = proposal.getRequestedTeam();

                    //팀 유저 가져오기
                    List<TeamUser> requestingTeamUsers = teamUserRepository.findAllByTeam(requestingTeam);
                    List<TeamUser> requestedTeamUsers = teamUserRepository.findAllByTeam(requestedTeam);

                    //팀 지역 가져오기
                    List<TeamRegion> requestingTeamRegions = teamRegionRepository.findAllByTeam(requestingTeam);
                    List<TeamRegion> requestedTeamRegions = teamRegionRepository.findAllByTeam(requestedTeam);


                    return proposalMapper.toListDto(
                            proposal,
                            getListTeamDto(requestingTeam, requestingTeamUsers, requestingTeamRegions),
                            getListTeamDto(requestedTeam, requestedTeamUsers, requestedTeamRegions)
                    );
                })
                .collect(Collectors.toList());
    }

    private ProposalResponse.ListDto.TeamDto getListTeamDto(Team team, List<TeamUser> teamUsers, List<TeamRegion> teamRegions) {
        return proposalMapper.toListTeamDto(
                team,
                getAverageAge(teamUsers),
                teamUsers.stream()
                        .map(TeamUser::getUser)
                        .map(teamUser -> proposalMapper.toListUserDto(teamUser, proposalMapper.toListUniversityDto(teamUser.getUniversity())))
                        .collect(Collectors.toList()),
                teamRegions.stream()
                        .map(teamRegion -> proposalMapper.toListRegionDto(teamRegion.getRegion()))
                        .collect(Collectors.toList())
        );
    }

    private static Double getAverageAge(List<TeamUser> teamUsers) {
        return teamUsers.stream()
                .map(TeamUser::getUser)
                .collect(Collectors.averagingDouble(ProposalService::getAge));
    }

    private static int getAge(User user) {
        return DateTimeUtils.nowFromZone().getYear() - user.getPersonalInfo().getBirthYear().getValue();
    }
    private void validateAlreadySentProposal(Long requestingTeamId, Long requestedTeamId) {
        proposalRepository.findByRequestingTeamIdAndRequestedTeamId(requestingTeamId, requestedTeamId)
                .ifPresent(p -> {
                    throw new IllegalArgumentException("이미 매칭 제안 요청을 보냈습니다.");
                });
    }

    private void validateUserInTeam(User user, List<TeamUser> requestingTeamUsers) {
        if(requestingTeamUsers.stream().noneMatch(teamUser -> teamUser.getUser().getId().equals(user.getId()))) {
            throw new IllegalArgumentException("유저가 해당 팀에 속해있지 않습니다.");
        }
    }
}

