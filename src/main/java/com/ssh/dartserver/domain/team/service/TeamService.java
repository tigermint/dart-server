package com.ssh.dartserver.domain.team.service;

import com.ssh.dartserver.domain.question.dto.mapper.QuestionMapper;
import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamRegion;
import com.ssh.dartserver.domain.team.dto.BlindDateTeamResponse;
import com.ssh.dartserver.domain.team.dto.BlindDateTeamDetailResponse;
import com.ssh.dartserver.domain.team.dto.BlindDateUserDetailResponse;
import com.ssh.dartserver.domain.team.dto.BlindDateUserResponse;
import com.ssh.dartserver.domain.team.dto.RegionResponse;
import com.ssh.dartserver.domain.team.dto.mapper.RegionMapper;
import com.ssh.dartserver.domain.team.dto.mapper.TeamMapper;
import com.ssh.dartserver.domain.team.infra.TeamRegionRepository;
import com.ssh.dartserver.domain.team.infra.TeamRepository;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import com.ssh.dartserver.domain.user.dto.ProfileQuestionResponse;
import com.ssh.dartserver.domain.user.dto.mapper.ProfileQuestionMapper;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.global.error.TeamNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TeamService {
    private static final long ALL_REGIONS = 0;

    private final TeamRepository teamRepository;
    private final TeamRegionRepository teamRegionRepository;
    private final UserRepository userRepository;

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

    public BlindDateTeamDetailResponse readTeam(long teamId) {
        Team team = teamRepository.findById(teamId).orElseThrow(() -> new TeamNotFoundException("존재하지 않는 팀입니다."));
        return getBlindDateTeamDetail(team);
    }

    private BlindDateTeamResponse getBlindDateTeam(Team team) {
        List<Integer> userAges = new ArrayList<>();
        List<BlindDateUserResponse> users = new ArrayList<>();
        AtomicBoolean anyoneCertified = new AtomicBoolean(false);
        AtomicReference<String> universityName = new AtomicReference<>("");

        team.getTeamUsersCombinationHash().getUsersId().forEach(userId -> {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return;
            }

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

    private BlindDateTeamDetailResponse getBlindDateTeamDetail(Team team) {
        List<Integer> userAges = new ArrayList<>();
        List<BlindDateUserDetailResponse> users = new ArrayList<>();
        AtomicBoolean anyoneCertified = new AtomicBoolean(false);
        AtomicReference<String> universityName = new AtomicReference<>("");

        team.getTeamUsersCombinationHash().getUsersId().forEach(userId -> {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return;
            }

            userAges.add(user.getPersonalInfo().getBirthYear().getValue());
            universityName.set(user.getUniversity().getName());
            if (!anyoneCertified.get() && user.getStudentVerificationInfo()
                    .getStudentIdCardVerificationStatus().isVerificationSuccess()) {
                anyoneCertified.set(true);
            }
            BlindDateUserDetailResponse blindDateUserDetailResponse = getTeamUserDetail(user);
            users.add(blindDateUserDetailResponse);
        });

        List<RegionResponse> regionResponses = getTeamRegionResponses(team);
        return teamMapper.toBlindDateTeamDetailResponse(team, getAverageAge(userAges), regionResponses, universityName.get(), anyoneCertified.get(), users);
    }

    private BlindDateUserResponse getTeamUserSimple(User user) {
        String nicknameOrName = user.getNicknameOrElseName();
        return BlindDateUserResponse.builder()
                .id(user.getId())
                .name(nicknameOrName)
                .department(user.getUniversity().getDepartment())
                .profileImageUrl(user.getPersonalInfo().getProfileImageUrl().getValue())
                .build();
    }

    private BlindDateUserDetailResponse getTeamUserDetail(User user) {
        String nicknameOrName = user.getNicknameOrElseName();
        List<ProfileQuestionResponse> profileQuestionResponses = user.getProfileQuestions()
                .getValues()
                .stream()
                .map((profileQuestion ->
                        profileQuestionMapper.toProfileQuestionResponse(
                                questionMapper.toQuestionResponse(profileQuestion.getQuestion()),
                                profileQuestion.getCount()
                        )
                ))
                .collect(Collectors.toList());

        return BlindDateUserDetailResponse.builder()
                .id(user.getId())
                .name(nicknameOrName)
                .birthYear(user.getPersonalInfo().getBirthYear().getValue())
                .department(user.getUniversity().getDepartment())
                .profileImageUrl(user.getPersonalInfo().getProfileImageUrl().getValue())
                .isCertifiedUser(user.getStudentVerificationInfo().isCertified())
                .profileQuestionResponses(profileQuestionResponses)
                .build();
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

    private static double getAverageAge(List<Integer> userAges) {
        return userAges.stream()
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);
    }
}
