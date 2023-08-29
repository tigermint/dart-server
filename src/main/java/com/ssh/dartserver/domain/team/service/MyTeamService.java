package com.ssh.dartserver.domain.team.service;

import com.ssh.dartserver.domain.question.dto.mapper.QuestionMapper;
import com.ssh.dartserver.domain.team.domain.*;
import com.ssh.dartserver.domain.team.dto.RegionResponse;
import com.ssh.dartserver.domain.team.dto.TeamRequest;
import com.ssh.dartserver.domain.team.dto.TeamResponse;
import com.ssh.dartserver.domain.team.dto.mapper.RegionMapper;
import com.ssh.dartserver.domain.team.dto.mapper.TeamMapper;
import com.ssh.dartserver.domain.team.infra.RegionRepository;
import com.ssh.dartserver.domain.team.infra.TeamRegionRepository;
import com.ssh.dartserver.domain.team.infra.TeamRepository;
import com.ssh.dartserver.domain.team.infra.TeamUserRepository;
import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.university.dto.mapper.UniversityMapper;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.profilequestions.ProfileQuestion;
import com.ssh.dartserver.domain.user.dto.UserProfileResponse;
import com.ssh.dartserver.domain.user.dto.mapper.ProfileQuestionMapper;
import com.ssh.dartserver.domain.user.dto.mapper.UserMapper;
import com.ssh.dartserver.domain.user.infra.ProfileQuestionRepository;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MyTeamService {
    private final RegionRepository regionRepository;
    private final UserRepository userRepository;
    private final TeamRepository teamRepository;
    private final TeamRegionRepository teamRegionRepository;
    private final TeamUserRepository teamUserRepository;
    private final ProfileQuestionRepository profileQuestionRepository;

    private final UserMapper userMapper;
    private final TeamMapper teamMapper;
    private final RegionMapper regionMapper;
    private final UniversityMapper universityMapper;
    private final ProfileQuestionMapper profileQuestionMapper;
    private final QuestionMapper questionMapper;

    @Transactional
    public Long createTeam(User user, TeamRequest request) {
        List<Long> userIds = request.getUserIds();
        List<Long> regionIds = request.getRegionIds();
        userIds.add(user.getId());

        List<User> users = userRepository.findAllByIdIn(userIds);
        List<Region> regions = regionRepository.findAllByIdIn(regionIds);

        //validation
        validateTeamUserSize(userIds);
        validateTeamUserDuplicate(userIds);
        validateTeamUserCombinationExists(userIds);
        validateTeamUserAreSameGender(users);
        validateTeamUserAreSameUniversity(users);

        //create Team
        Team team = Team.builder()
                .name(request.getName())
                .isVisibleToSameUniversity(request.getIsVisibleToSameUniversity())
                .university(user.getUniversity())
                .teamUsersCombinationHash(TeamUsersCombinationHash.of(userIds))
                .build();
        Team savedTeam = teamRepository.save(team);

        //create teamRegion, teamUser
        List<TeamRegion> teamRegions = getTeamRegions(regionIds, regions, savedTeam);
        List<TeamUser> teamUsers = getTeamUsers(userIds, users, savedTeam);
        teamRegionRepository.saveAll(teamRegions);
        teamUserRepository.saveAll(teamUsers);
        return savedTeam.getId();
    }


    public TeamResponse readTeam(User user, Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다."));
        List<TeamUser> teamUsers = teamUserRepository.findAllByTeam(team);
        List<TeamRegion> teamRegions = teamRegionRepository.findAllByTeam(team);

        validateTeamUserIsPartOfTeam(user, teamUsers);

        return getTeamResponse(team, teamRegions, teamUsers);
    }

    public List<TeamResponse> listTeam(User user) {
        List<TeamUser> teamUsers = teamUserRepository.findAllByUser(user);
        List<Team> myTeams = teamUsers.stream()
                .map(TeamUser::getTeam)
                .distinct()
                .collect(Collectors.toList());

        return myTeams.stream()
                .map(myTeam -> {
                    List<TeamRegion> myTeamRegions = teamRegionRepository.findAllByTeam(myTeam);
                    List<TeamUser> myTeamUsers = teamUserRepository.findAllByTeam(myTeam);
                    return getTeamResponse(myTeam, myTeamRegions, myTeamUsers);
                })
                .collect(Collectors.toList());
    }
    @Transactional
    public TeamResponse updateTeam(User user, Long teamId, TeamRequest request) {
        //필요 정보 조회
        List<Long> userIds = request.getUserIds();
        List<Long> regionIds = request.getRegionIds();
        userIds.add(user.getId());

        List<User> users = userRepository.findAllByIdIn(userIds);
        List<Region> regions = regionRepository.findAllByIdIn(regionIds);

        //기존 팀 정보 조회
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다."));
        List<TeamRegion> teamRegions = teamRegionRepository.findAllByTeamId(teamId);
        List<TeamUser> teamUsers = teamUserRepository.findAllByTeamId(teamId);

        //업데이트 될 팀원들 validation
        validateTeamUserSize(userIds);
        validateTeamUserDuplicate(userIds);

        validateTeamUserCombinationExists(userIds);
        validateTeamUserAreSameUniversity(users);
        validateTeamUserAreSameGender(users);

        //내가 현재 팀에 속해 있는지 validation -> 그래야 수정 가능하니깐
        validateTeamUserIsPartOfTeam(user, teamUsers);

        //update
        team.update(request.getName(), request.getIsVisibleToSameUniversity(), TeamUsersCombinationHash.of(userIds));

        //team 관련 정보 update
        teamUserRepository.deleteAll(teamUsers);
        teamRegionRepository.deleteAll(teamRegions);

        List<TeamRegion> updateTeamRegions = getTeamRegions(regionIds, regions, team);
        List<TeamUser> updateTeamUsers = getTeamUsers(userIds, users, team);
        teamRegionRepository.saveAll(updateTeamRegions);
        teamUserRepository.saveAll(updateTeamUsers);

        return getTeamResponse(team, updateTeamRegions, updateTeamUsers);
    }

    @Transactional
    public void deleteTeam(User user, Long teamId) {
        validateTeamUserIsPartOfTeam(user, teamUserRepository.findAllByTeamId(teamId));
        teamUserRepository.deleteAllByTeamId(teamId);
        teamRegionRepository.deleteAllByTeamId(teamId);
        teamRepository.deleteById(teamId);
    }

    private TeamResponse getTeamResponse(Team team, List<TeamRegion> teamRegions, List<TeamUser> teamUsers) {

        List<RegionResponse> regionResponses = teamRegions.stream()
                .map(TeamRegion::getRegion)
                .map(regionMapper::toRegionResponse)
                .collect(Collectors.toList());

        List<UserProfileResponse> userProfileResponse = teamUsers.stream()
                .map(TeamUser::getUser)
                .map(this::getUserProfileResponse)
                .collect(Collectors.toList());

        return teamMapper.toTeamResponse(team, regionResponses, userProfileResponse);
    }
    private UserProfileResponse getUserProfileResponse(User user) {

        List<ProfileQuestion> profileQuestions = profileQuestionRepository.findAllByUser(user);

        return userMapper.toUserProfileResponse(
                userMapper.toUserResponse(user),
                universityMapper.toUniversityResponse(user.getUniversity()),
                profileQuestions.stream()
                        .map(profileQuestion ->
                                profileQuestionMapper.toProfileQuestionResponse(
                                        questionMapper.toQuestionResponse(profileQuestion.getQuestion()),
                                        profileQuestion.getCount()
                                ))
                        .collect(Collectors.toList())
        );
    }

    private List<TeamUser> getTeamUsers(List<Long> userIds, List<User> users, Team team) {
        if(users.size() != userIds.size()) {
            throw new IllegalArgumentException("존재하지 않는 유저가 있습니다.");
        }
        return users.stream()
                .map(user -> TeamUser.builder()
                        .team(team)
                        .user(user)
                        .build())
                .collect(Collectors.toList());
    }


    private List<TeamRegion> getTeamRegions(List<Long> regionIds, List<Region> regions, Team team) {
        if(regions.size() != regionIds.size()) {
            throw new IllegalArgumentException("존재하지 않는 지역이 있습니다.");
        }
        return regions.stream()
                .map(region -> TeamRegion.builder()
                        .team(team)
                        .region(region)
                        .build())
                .collect(Collectors.toList());
    }

    private void validateTeamUserAreSameUniversity(List<User> users) {
        List<String> universityNames = users.stream()
                .map(User::getUniversity)
                .map(University::getName)
                .collect(Collectors.toList());

        if (universityNames.stream().distinct().count() != 1) {
            throw new IllegalArgumentException("유저들이 모두 같은 학교가 아닙니다.");
        }
    }
    private void validateTeamUserCombinationExists(List<Long> userIds) {
        TeamUsersCombinationHash teamUsersCombinationHash = TeamUsersCombinationHash.of(userIds);

        if (userIds.size() > 1 && teamRepository.findByTeamUsersCombinationHash(teamUsersCombinationHash).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 팀입니다.");
        }
    }
    private void validateTeamUserSize(List<Long> userIds) {
        if (userIds.size() > 3 || userIds.size() < 2) {
            throw new IllegalArgumentException("팀원은 2명 이상 3명 이하여야 합니다.");
        }
    }
    private void validateTeamUserDuplicate(List<Long> userIds) {
        if(userIds.size() != userIds.stream().distinct().count()) {
            throw new IllegalArgumentException("중복된 유저가 존재합니다.");
        }
    }
    private void validateTeamUserIsPartOfTeam(User user, List<TeamUser> teamUsers) {

        List<Long> teamUserIds = teamUsers.stream()
                .map(TeamUser::getUser)
                .map(User::getId)
                .collect(Collectors.toList());

        if(!teamUserIds.contains(user.getId())) {
            throw new IllegalArgumentException("팀에 속해있지 않은 유저입니다.");
        }
    }
    private void validateTeamUserAreSameGender(List<User> users) {
        long count = users.stream()
                .map(user -> user.getPersonalInfo().getGender())
                .distinct()
                .count();

        if(count != 1) {
            throw new IllegalArgumentException("팀원들의 성별이 모두 같지 않습니다.");
        }
    }
}
