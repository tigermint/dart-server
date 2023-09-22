package com.ssh.dartserver.domain.team.service;

import com.ssh.dartserver.domain.proposal.domain.Proposal;
import com.ssh.dartserver.domain.proposal.infra.ProposalRepository;
import com.ssh.dartserver.domain.question.dto.mapper.QuestionMapper;
import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamRegion;
import com.ssh.dartserver.domain.team.domain.TeamUser;
import com.ssh.dartserver.domain.team.dto.*;
import com.ssh.dartserver.domain.team.dto.mapper.RegionMapper;
import com.ssh.dartserver.domain.team.dto.mapper.TeamMapper;
import com.ssh.dartserver.domain.team.infra.SingleTeamFriendRepository;
import com.ssh.dartserver.domain.team.infra.TeamRegionRepository;
import com.ssh.dartserver.domain.team.infra.TeamRepository;
import com.ssh.dartserver.domain.team.infra.TeamUserRepository;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import com.ssh.dartserver.domain.user.domain.profilequestions.ProfileQuestions;
import com.ssh.dartserver.domain.user.dto.ProfileQuestionResponse;
import com.ssh.dartserver.domain.user.dto.mapper.ProfileQuestionMapper;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.global.error.TeamNotFoundException;
import com.ssh.dartserver.global.util.DateTimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {
    private static final long ALL_REGIONS = 0;

    private final TeamRepository teamRepository;
    private final TeamRegionRepository teamRegionRepository;
    private final UserRepository userRepository;
    private final SingleTeamFriendRepository singleTeamFriendRepository;
    private final TeamUserRepository teamUserRepository;
    private final ProposalRepository proposalRepository;

    private final RegionMapper regionMapper;
    private final ProfileQuestionMapper profileQuestionMapper;
    private final QuestionMapper questionMapper;
    private final TeamMapper teamMapper;

    public Long countAllTeams() {
        return teamRepository.count() * 2 + 50;
    }

    public Page<BlindDateTeamResponse> listVisibleTeams(long universityId, Gender myGender, long regionId, Pageable pageable) {
        Page<Team> allVisibleTeams = getTeams(universityId, myGender, regionId, pageable);

        List<BlindDateTeamResponse> visibleTeams = allVisibleTeams.getContent()
                .stream()
                .map(this::getBlindDateTeam)
                .collect(Collectors.toList());

        return new PageImpl<>(visibleTeams, pageable, allVisibleTeams.getTotalElements());
    }

    public BlindDateTeamDetailResponse readTeam(User user, long teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamNotFoundException("존재하지 않는 팀입니다."));
        return getBlindDateTeamDetail(user, team);
    }

    private BlindDateTeamResponse getBlindDateTeam(Team team) {
        List<Integer> userAges = new ArrayList<>();
        List<BlindDateUserResponse> users = new ArrayList<>();
        AtomicBoolean anyoneCertified = new AtomicBoolean(false);
        AtomicReference<String> universityName = new AtomicReference<>("");

        List<User> teamUsers = Optional.of(team.getTeamUsersCombinationHash().getUsersId())
                .filter(teamUserIds -> teamUserIds.size() == 1)
                .map(teamUserIds -> getSingleTeamUserProfileResponse(team, teamUserIds))
                .orElseGet(() -> getMultipleTeamUserProfileResponse(team.getTeamUsersCombinationHash().getUsersId()));

        teamUsers.forEach(user -> {
            userAges.add(user.getPersonalInfo().getBirthYear().getValue());
            universityName.set(user.getUniversity().getName());
            if (!anyoneCertified.get() && user.getStudentVerificationInfo()
                    .getStudentIdCardVerificationStatus().isVerificationSuccess()) {
                anyoneCertified.set(true);
            }
            BlindDateUserResponse blindDateUserResponse = getTeamUserSimple(user);
            users.add(blindDateUserResponse);
        });

        List<RegionResponse> regionResponses = getTeamRegionResponses(team);
        return teamMapper.toBlindDateTeamResponse(team, getAverageAge(userAges), regionResponses, universityName.get(), anyoneCertified.get(), users);
    }

    private BlindDateTeamDetailResponse getBlindDateTeamDetail(User user, Team team) {
        List<Integer> userAges = new ArrayList<>();
        List<BlindDateUserDetailResponse> users = new ArrayList<>();
        AtomicBoolean anyoneCertified = new AtomicBoolean(false);
        AtomicReference<String> universityName = new AtomicReference<>("");

        List<User> teamUsers = Optional.of(team.getTeamUsersCombinationHash().getUsersId())
                .filter(teamUserIds -> teamUserIds.size() == 1)
                .map(teamUserIds -> getSingleTeamUserProfileResponse(team, teamUserIds))
                .orElseGet(() -> getMultipleTeamUserProfileResponse(team.getTeamUsersCombinationHash().getUsersId()));

        teamUsers.forEach(teamUser -> {
            userAges.add(teamUser.getPersonalInfo().getBirthYear().getValue());
            universityName.set(teamUser.getUniversity().getName());
            if (!anyoneCertified.get() && teamUser.getStudentVerificationInfo().getStudentIdCardVerificationStatus().isVerificationSuccess()) {
                anyoneCertified.set(true);
            }
            BlindDateUserDetailResponse blindDateUserDetailResponse = getTeamUserDetail(teamUser);
            users.add(blindDateUserDetailResponse);
        });

        // 내가 속한 팀 가져오기
        List<Team> myTeams = teamUserRepository.findAllByUser(user).stream()
                .map(TeamUser::getTeam)
                .distinct()
                .collect(Collectors.toList());

        List<Proposal> proposals = proposalRepository.findByRequestingTeamOrRequestedTeam(team, team);

        List<Proposal> proposalsInMyTeams = proposals.stream()
                .filter(proposal -> myTeams.contains(proposal.getRequestedTeam()) || myTeams.contains(proposal.getRequestingTeam()))
                .collect(Collectors.toList());

        List<RegionResponse> regionResponses = getTeamRegionResponses(team);
        return teamMapper.toBlindDateTeamDetailResponse(team, getAverageAge(userAges), regionResponses, universityName.get(), anyoneCertified.get(), users, !proposalsInMyTeams.isEmpty());
    }

    private List<User> getSingleTeamUserProfileResponse(Team team, List<Long> teamUserIds) {
        return Stream.concat(
                        userRepository.findAllByIdIn(teamUserIds).stream(),
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
                .collect(Collectors.toList());
    }

    private List<User> getMultipleTeamUserProfileResponse(List<Long> teamUserIds) {
        return userRepository.findAllByIdIn(teamUserIds);
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

    private Page<Team> getTeams(long universityId, Gender myGender, long regionId, Pageable pageable) {
        if (regionId == ALL_REGIONS) {
            return teamRepository.findAllVisibleTeams(universityId, myGender, pageable);
        }
        return teamRepository.findAllVisibleTeamsByRegionId(universityId, myGender, regionId, pageable);
    }

    private List<RegionResponse> getTeamRegionResponses(Team team) {
        return teamRegionRepository.findAllByTeam(team)
                .stream()
                .map((TeamRegion::getRegion))
                .map(regionMapper::toRegionResponse)
                .collect(Collectors.toList());
    }

    private static double getAverageAge(List<Integer> userBirthYears) {
        return userBirthYears.stream()
                .map(TeamService::getAge)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }

    private static int getAge(int value) {
        return DateTimeUtil.nowFromZone().getYear() - value;
    }

}
