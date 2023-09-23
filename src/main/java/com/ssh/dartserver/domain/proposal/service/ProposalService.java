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
import com.ssh.dartserver.domain.team.infra.SingleTeamFriendRepository;
import com.ssh.dartserver.domain.team.infra.TeamRegionRepository;
import com.ssh.dartserver.domain.team.infra.TeamUserRepository;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.global.infra.notification.PlatformNotification;
import com.ssh.dartserver.global.util.TeamAverageAgeCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProposalService {
    private static final int PROPOSAL_POINT = 0;
    private static final String PROPOSAL_CONTENTS = "매칭 제안이 도착했어요 💌";

    private final ProposalRepository proposalRepository;
    private final TeamUserRepository teamUserRepository;
    private final TeamRegionRepository teamRegionRepository;
    private final UserRepository userRepository;
    private final SingleTeamFriendRepository singleTeamFriendRepository;
    private final TeamAverageAgeCalculator teamAverageAgeCalculator;

    private final PlatformNotification notification;

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

        List<Long> requestedTeamUserIds = requestedTeamUsers.stream()
                .map(TeamUser::getUser)
                .map(User::getId)
                .collect(Collectors.toList());

        CompletableFuture.runAsync(() ->
                notification.postNotificationSpecificDevice(requestedTeamUserIds, PROPOSAL_CONTENTS)
        );

        return proposal.getId();
    }


    public List<ProposalResponse.ListDto> listSentProposal(User user) {
        String userIdPattern = "%-" + user.getId() + "-%";
        return getListDtos(proposalRepository.findAllRequestingProposalByUserIdPatternAndProposalStatus(userIdPattern, ProposalStatus.PROPOSAL_IN_PROGRESS));
    }

    public List<ProposalResponse.ListDto> listReceivedProposal(User user) {
        String userIdPattern = "%-" + user.getId() + "-%";
        return getListDtos(proposalRepository.findAllRequestedProposalByUserIdPatternAndProposalStatus(userIdPattern, ProposalStatus.PROPOSAL_IN_PROGRESS));
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
                .flatMap(proposal -> {
                    //팀 가져오기
                    Team requestingTeam = proposal.getRequestingTeam();
                    Team requestedTeam = proposal.getRequestedTeam();

                    //팀 유저 가져오기
                    List<TeamUser> requestingTeamUsers = teamUserRepository.findAllByTeam(requestingTeam);
                    List<TeamUser> requestedTeamUsers = teamUserRepository.findAllByTeam(requestedTeam);

                    //팀 지역 가져오기
                    List<TeamRegion> requestingTeamRegions = teamRegionRepository.findAllByTeam(requestingTeam);
                    List<TeamRegion> requestedTeamRegions = teamRegionRepository.findAllByTeam(requestedTeam);

                    ProposalResponse.ListDto.TeamDto requestingTeamDto = getListTeamDto(requestingTeam, requestingTeamUsers, requestingTeamRegions);
                    ProposalResponse.ListDto.TeamDto requestedTeamDto = getListTeamDto(requestedTeam, requestedTeamUsers, requestedTeamRegions);

                    if (requestingTeamDto == null || requestedTeamDto == null) {
                        return Stream.empty();
                    }
                    return Stream.of(proposalMapper.toListDto(
                            proposal,
                            requestingTeamDto,
                            requestedTeamDto
                    ));
                })
                .collect(Collectors.toList());
    }

    private ProposalResponse.ListDto.TeamDto getListTeamDto(Team team, List<TeamUser> teamUsers, List<TeamRegion> teamRegions) {
        return Optional.ofNullable(team)
                .map(t -> proposalMapper.toListTeamDto(
                        t,
                        teamAverageAgeCalculator.getAverageAge(teamUsers),
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
                        singleTeamFriendRepository.findAllByTeam(team).stream()
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


    private void validateAlreadySentProposal(Long requestingTeamId, Long requestedTeamId) {
        proposalRepository.findByRequestingTeamIdAndRequestedTeamId(requestingTeamId, requestedTeamId)
                .ifPresent(p -> {
                    throw new IllegalArgumentException("이미 매칭 제안 요청을 보냈습니다.");
                });
    }

    private void validateUserInTeam(User user, List<TeamUser> requestingTeamUsers) {
        if (requestingTeamUsers.stream().noneMatch(teamUser -> teamUser.getUser().getId().equals(user.getId()))) {
            throw new IllegalArgumentException("유저가 해당 팀에 속해있지 않습니다.");
        }
    }
}
