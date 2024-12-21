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
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final BlindDateTeamReader blindDateTeamReader;

    private final ProposalMapper proposalMapper;

    private final PlatformNotification notification;
    private final TeamAverageAgeCalculator teamAverageAgeCalculator;

    @Transactional
    public Long createProposal(User user, ProposalRequest.Create request) {
        validateAlreadySentProposal(request.getRequestingTeamId(), request.getRequestedTeamId());

        // 모든 팀원을 가져오고, 요청한 유저가 생성한 팀인지 확인하기
        BlindDateTeamInfo requestedTeam = blindDateTeamReader.getTeamInfo(request.getRequestedTeamId(), user);
        BlindDateTeamInfo requestingTeam = blindDateTeamReader.getTeamInfo(request.getRequestingTeamId(), user);
        if (requestingTeam.leaderId() != user.getId()) {
            throw new IllegalArgumentException("유저가 해당 팀에 속해있지 않습니다. (userId:" + user.getId() + ")");
        }

        // 포인트 차감하기
        user.subtractPoint(PROPOSAL_POINT);

        // 새로운 호감 생성하기
        Proposal proposal = Proposal.builder()
                .proposalStatus(ProposalStatus.PROPOSAL_IN_PROGRESS)
                .requestingTeam(teamRepository.getById(requestingTeam.id()))  // TODO 호감 생성 부분 동작하도록 수정
                .requestedTeam(teamRepository.getById(requestedTeam.id()))
                .build();

        userRepository.save(user);
        proposalRepository.save(proposal);

        CompletableFuture.runAsync(() ->
                notification.postNotificationSpecificDevice(requestedTeam.leaderId(), PROPOSAL_CONTENTS)
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

    private void validateAlreadySentProposal(Long requestingTeamId, Long requestedTeamId) {
        proposalRepository.findByRequestingTeamIdAndRequestedTeamId(requestingTeamId, requestedTeamId)
                .ifPresent(p -> {
                    throw new IllegalArgumentException("이미 매칭 제안 요청을 보냈습니다.");
                });
    }

    private void validateUserInTeam(User user, List<TeamUser> requestingTeamUsers) {
        if (requestingTeamUsers.stream().noneMatch(teamUser -> teamUser.getUser().getId().equals(user.getId()))) {
            throw new IllegalArgumentException("유저가 해당 팀에 속해있지 않습니다. (userId:" + user.getId() + ")");
        }
    }
}

