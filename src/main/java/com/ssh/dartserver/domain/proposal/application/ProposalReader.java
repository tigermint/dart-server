package com.ssh.dartserver.domain.proposal.application;

import com.ssh.dartserver.domain.proposal.domain.Proposal;
import com.ssh.dartserver.domain.proposal.domain.ProposalStatus;
import com.ssh.dartserver.domain.proposal.infra.ProposalRepository;
import com.ssh.dartserver.domain.proposal.presentation.response.ProposalResponse;
import com.ssh.dartserver.domain.team.domain.SingleTeamFriend;
import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamRegion;
import com.ssh.dartserver.domain.team.domain.TeamUser;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.personalinfo.BirthYear;
import com.ssh.dartserver.global.util.TeamAverageAgeCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProposalReader {

    private final ProposalRepository proposalRepository;
    private final ProposalMapper proposalMapper;
    private final TeamAverageAgeCalculator teamAverageAgeCalculator;

    @Transactional(readOnly = true)
    public List<ProposalResponse.ListDto> listSentProposal(User user) {
        List<Proposal> proposals = new ArrayList<>();

        // v2
        proposals = proposalRepository.findByProposalStatusAndRequestingTeam_Leader_Id(ProposalStatus.PROPOSAL_IN_PROGRESS, user.getId());

        // v1
        if (proposals.isEmpty()) {
            String userIdPattern = "%-" + user.getId() + "-%";
            proposals = proposalRepository.findAllRequestingProposalByUserIdPatternAndProposalStatus(userIdPattern, ProposalStatus.PROPOSAL_IN_PROGRESS);
        }

        return getListDtos(proposals);
    }

    @Transactional(readOnly = true)
    public List<ProposalResponse.ListDto> listReceivedProposal(User user) {
        List<Proposal> proposals = new ArrayList<>();

        // v2
        proposals = proposalRepository.findByProposalStatusAndRequestedTeam_Leader_Id(ProposalStatus.PROPOSAL_IN_PROGRESS, user.getId());

        // v1
        if (proposals.isEmpty()) {
            String userIdPattern = "%-" + user.getId() + "-%";
            proposals = proposalRepository.findAllRequestedProposalByUserIdPatternAndProposalStatus(userIdPattern, ProposalStatus.PROPOSAL_IN_PROGRESS);
        }

        return getListDtos(proposals);
    }

    // TODO 조회 로직 리팩터링 (Calculator쪽으로 책임을 분리한다던가..)
    private List<ProposalResponse.ListDto> getListDtos(List<Proposal> proposals) {
        return proposals.stream()
                .flatMap(proposal -> {
                    Team requestingTeam = proposal.getRequestingTeam();
                    Team requestedTeam = proposal.getRequestedTeam();

                    if(requestingTeam == null || requestedTeam == null) {
                        return Stream.empty();
                    }

                    return Stream.of(proposalMapper.toListDto(
                            proposal,
                            getListTeamDto(requestingTeam, requestingTeam.getTeamUsers(), requestingTeam.getTeamRegions()),
                            getListTeamDto(requestedTeam, requestedTeam.getTeamUsers(), requestedTeam.getTeamRegions())
                    ));
                })
                .collect(Collectors.toList());
    }

    private ProposalResponse.ListDto.TeamDto getListTeamDto(Team team, List<TeamUser> teamUsers, List<TeamRegion> teamRegions) {
        return Optional.ofNullable(team)
                .map(t -> proposalMapper.toListTeamDto(
                        t,
                        Optional.of(teamUsers)
                                .filter(users -> users.size() == 1)
                                .map(this::getAverageAgeOfSingleTeamUsers)
                                .orElseGet(() -> getAverageAgeOfMultipleTeamUsers(teamUsers)),
                        Optional.of(teamUsers)
                                .filter(users -> users.size() == 1)
                                .map(users -> getListSingleTeamUserDto(team, users))
                                .orElseGet(() -> getListMultipleTeamUserDto(teamUsers)),
                        teamRegions.stream()
                                .map(teamRegion -> proposalMapper.toListRegionDto(teamRegion.getRegion()))
                                .collect(Collectors.toList())
                ))
                .orElse(null);
    }

    private List<ProposalResponse.ListDto.UserDto> getListSingleTeamUserDto(Team team, List<TeamUser> teamUsers) {
        return Stream.concat(
                        teamUsers.stream()
                                .map(TeamUser::getUser),
                        team.getSingleTeamFriends().stream()
                                .map(singleTeamFriend ->
                                        User.createSingleTeamFriendUser(
                                                singleTeamFriend.getNickname().getValue(),
                                                singleTeamFriend.getBirthYear().getValue(),
                                                singleTeamFriend.getProfileImageUrl().getValue(),
                                                singleTeamFriend.getUniversity()
                                        )
                                )
                )
                .map(teamUser -> proposalMapper.toListUserDto(teamUser, proposalMapper.toListUniversityDto(teamUser.getUniversity())))
                .collect(Collectors.toList());
    }

    private List<ProposalResponse.ListDto.UserDto> getListMultipleTeamUserDto(List<TeamUser> teamUsers) {
        return teamUsers.stream()
                .map(TeamUser::getUser)
                .map(teamUser -> proposalMapper.toListUserDto(teamUser, proposalMapper.toListUniversityDto(teamUser.getUniversity())))
                .collect(Collectors.toList());
    }

    private Double getAverageAgeOfMultipleTeamUsers(List<TeamUser> teamUsers) {
        return teamAverageAgeCalculator.getAverageAge(
                teamUsers.stream()
                        .map(TeamUser::getUser)
                        .map(user -> user.getPersonalInfo().getBirthYear().getValue())
                        .collect(Collectors.toList())
        );
    }

    private Double getAverageAgeOfSingleTeamUsers(List<TeamUser> teamUsers) {
        return teamAverageAgeCalculator.getAverageAge(
                Stream.concat(
                                teamUsers.get(0).getTeam().getSingleTeamFriends().stream()
                                        .map(SingleTeamFriend::getBirthYear)
                                        .map(BirthYear::getValue),
                                Stream.of(teamUsers.get(0).getUser().getPersonalInfo().getBirthYear().getValue())
                        )
                        .collect(Collectors.toList())
        );
    }

}
