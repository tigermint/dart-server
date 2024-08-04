package com.ssh.dartserver.domain.team.application;

import com.ssh.dartserver.domain.proposal.domain.Proposal;
import com.ssh.dartserver.domain.proposal.infra.ProposalRepository;
import com.ssh.dartserver.domain.question.application.QuestionMapper;
import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamRegion;
import com.ssh.dartserver.domain.team.domain.TeamUser;
import com.ssh.dartserver.domain.team.infra.TeamRegionRepository;
import com.ssh.dartserver.domain.team.infra.TeamRepository;
import com.ssh.dartserver.domain.team.infra.TeamUserRepository;
import com.ssh.dartserver.domain.team.domain.TeamSearchCondition;
import com.ssh.dartserver.domain.team.presentation.response.*;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.profilequestions.ProfileQuestions;
import com.ssh.dartserver.domain.user.presentation.v1.response.ProfileQuestionResponse;
import com.ssh.dartserver.domain.user.application.ProfileQuestionMapper;
import com.ssh.dartserver.global.util.TeamAverageAgeCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {

    private static final int VIEW_COUNT_INCREMENT = 1;

    private final TeamRepository teamRepository;
    private final TeamRegionRepository teamRegionRepository;
    private final TeamUserRepository teamUserRepository;
    private final ProposalRepository proposalRepository;

    private final RegionMapper regionMapper;
    private final ProfileQuestionMapper profileQuestionMapper;
    private final QuestionMapper questionMapper;
    private final TeamMapper teamMapper;

    private final TeamAverageAgeCalculator teamAverageAgeCalculator;
    private final TeamViewCountNotificationUtil teamViewCountNotificationUtil;

    public Long countAllTeam() {
        return teamRepository.count() * 2 + 50;
    }
  
    @Transactional
    public BlindDateTeamDetailResponse readTeam(User user, long teamId) {
        List<TeamUser> teamUsers = teamUserRepository.findAllByTeamId(teamId);
        List<TeamRegion> teamRegions = teamRegionRepository.findAllByTeamId(teamId);
        Team team = teamUsers.stream()
                .map(TeamUser::getTeam)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다."));

        team.increaseViewCount(VIEW_COUNT_INCREMENT);
        teamViewCountNotificationUtil.postNotificationOnViewCountMileStone(List.of(team));

        return getBlindDateTeamDetail(user, team, teamUsers, teamRegions);
    }

    @Transactional
    public Page<BlindDateTeamResponse> listVisibleTeam(User user, TeamSearchCondition condition, Pageable pageable) {
        Page<Team> allVisibleTeamPages = teamRepository.findAllVisibleTeam(user, condition, pageable);

        List<Team> visibleTeams = allVisibleTeamPages.getContent();
        teamRepository.increaseAllTeamViewCount(visibleTeams, VIEW_COUNT_INCREMENT);
        teamViewCountNotificationUtil.postNotificationOnViewCountMileStone(visibleTeams);

        Map<Team, List<TeamUser>> visibleTeamUsersMap = teamUserRepository.findAllByTeamIn(visibleTeams).stream()
                .collect(Collectors.groupingBy(TeamUser::getTeam));
        Map<Team, List<TeamRegion>> visibleTeamRegionsMap = teamRegionRepository.findAllByTeamIn(visibleTeams).stream()
                .collect(Collectors.groupingBy(TeamRegion::getTeam));

        List<BlindDateTeamResponse> blindDateTeamResponses = visibleTeams.stream()
                .map(visibleTeam -> {
                    List<TeamUser> teamUsers = visibleTeamUsersMap.getOrDefault(visibleTeam, Collections.emptyList());
                    List<TeamRegion> teamRegions = visibleTeamRegionsMap.getOrDefault(visibleTeam, Collections.emptyList());
                    return getBlindDateTeam(visibleTeam, teamUsers, teamRegions);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(blindDateTeamResponses, pageable, allVisibleTeamPages.getTotalElements());
    }

    private BlindDateTeamResponse getBlindDateTeam(Team team, List<TeamUser> teamUsers, List<TeamRegion> teamRegions) {
        List<Integer> userAges = new ArrayList<>();
        List<BlindDateUserResponse> blindDateUserResponses = new ArrayList<>();
        AtomicBoolean anyoneCertified = new AtomicBoolean(false);
        AtomicReference<String> universityName = new AtomicReference<>("");

        List<User> usersInTeam = Optional.of(teamUsers)
                .filter(users -> users.size() == 1)
                .map(users -> getSingleTeamUserProfileResponse(team, teamUsers))
                .orElseGet(() -> getMultipleTeamUserProfileResponse(teamUsers));

        usersInTeam.forEach(userInTeam -> {
            userAges.add(userInTeam.getPersonalInfo().getBirthYear().getValue());
            universityName.set(userInTeam.getUniversity().getName());
            if (!anyoneCertified.get() && userInTeam.getStudentVerificationInfo()
                    .getStudentIdCardVerificationStatus().isVerificationSuccess()) {
                anyoneCertified.set(true);
            }
            BlindDateUserResponse blindDateUserResponse = getTeamUserSimple(userInTeam);
            blindDateUserResponses.add(blindDateUserResponse);
        });

        return teamMapper.toBlindDateTeamResponse(
                team,
                teamAverageAgeCalculator.getAverageAge(userAges),
                getTeamRegionResponses(teamRegions),
                universityName.get(),
                anyoneCertified.get(),
                blindDateUserResponses);
    }

    private BlindDateTeamDetailResponse getBlindDateTeamDetail(User user, Team team, List<TeamUser> teamUsers, List<TeamRegion> teamRegions) {
        List<Integer> userAges = new ArrayList<>();
        List<BlindDateUserDetailResponse> blindDateUserDetailResponses = new ArrayList<>();
        AtomicBoolean anyoneCertified = new AtomicBoolean(false);
        AtomicReference<String> universityName = new AtomicReference<>("");

        List<User> usersInTeam = Optional.of(teamUsers)
                .filter(users -> users.size() == 1)
                .map(users -> getSingleTeamUserProfileResponse(team, teamUsers))
                .orElseGet(() -> getMultipleTeamUserProfileResponse(teamUsers));

        usersInTeam.forEach(userInTeam -> {
            userAges.add(userInTeam.getPersonalInfo().getBirthYear().getValue());
            universityName.set(userInTeam.getUniversity().getName());
            if (!anyoneCertified.get() && userInTeam.getStudentVerificationInfo().getStudentIdCardVerificationStatus().isVerificationSuccess()) {
                anyoneCertified.set(true);
            }
            BlindDateUserDetailResponse blindDateUserDetailResponse = getTeamUserDetail(userInTeam);
            blindDateUserDetailResponses.add(blindDateUserDetailResponse);
        });

        return teamMapper.toBlindDateTeamDetailResponse(
                team,
                teamAverageAgeCalculator.getAverageAge(userAges),
                getTeamRegionResponses(teamRegions),
                universityName.get(),
                anyoneCertified.get(),
                blindDateUserDetailResponses,
                hasProposalsForMyTeams(user, team)
        );
    }

    private List<User> getSingleTeamUserProfileResponse(Team team, List<TeamUser> teamUsers) {
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
                .collect(Collectors.toList());
    }

    private List<User> getMultipleTeamUserProfileResponse(List<TeamUser> teamUsers) {
        return teamUsers.stream()
                .map(TeamUser::getUser)
                .collect(Collectors.toList());
    }


    private BlindDateUserResponse getTeamUserSimple(User user) {
        String nicknameOrName = user.getNicknameOrElseName();
        return teamMapper.toBlindDateUserResponse(user, nicknameOrName);
    }

    private BlindDateUserDetailResponse getTeamUserDetail(User user) {
        String nicknameOrName = user.getNicknameOrElseName();

        List<ProfileQuestionResponse> profileQuestionResponses = Optional.ofNullable(user.getProfileQuestions())
                .map(ProfileQuestions::getValues)
                .orElse(Collections.emptyList())
                .stream()
                .map((profileQuestion ->
                        profileQuestionMapper.toProfileQuestionResponse(
                                questionMapper.toQuestionResponse(profileQuestion.getQuestion()),
                                profileQuestion.getCount()
                        )
                ))
                .collect(Collectors.toList());
        return teamMapper.toBlindDateUserDetailResponse(
                user,
                user.getStudentVerificationInfo().isCertified(),
                nicknameOrName,
                profileQuestionResponses
        );
    }

    private List<RegionResponse> getTeamRegionResponses(List<TeamRegion> teamRegions) {
        return teamRegions.stream()
                .map((TeamRegion::getRegion))
                .map(regionMapper::toRegionResponse)
                .collect(Collectors.toList());
    }

    private Boolean hasProposalsForMyTeams(User user, Team team) {
        Set<Team> myTeams = teamUserRepository.findAllByUser(user).stream()
                .map(TeamUser::getTeam)
                .collect(Collectors.toSet());
        List<Proposal> proposals = proposalRepository.findAllByRequestingTeamOrRequestedTeam(team, team);

        return proposals.stream()
                .anyMatch(proposal -> myTeams.contains(proposal.getRequestedTeam()) || myTeams.contains(proposal.getRequestingTeam()));
    }

}
