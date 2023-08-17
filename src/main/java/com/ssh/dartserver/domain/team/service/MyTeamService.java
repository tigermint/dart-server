package com.ssh.dartserver.domain.team.service;

import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamRegion;
import com.ssh.dartserver.domain.team.domain.TeamUser;
import com.ssh.dartserver.domain.team.domain.TeamUsersCombinationHash;
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
import com.ssh.dartserver.domain.university.infra.UniversityRepository;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.dto.UserWithUniversityResponse;
import com.ssh.dartserver.domain.user.dto.mapper.UserMapper;
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
    private final UniversityRepository universityRepository;

    private final UserMapper userMapper;
    private final TeamMapper teamMapper;
    private final RegionMapper regionMapper;
    private final UniversityMapper universityMapper;

    @Transactional
    public TeamResponse createTeam(User user, TeamRequest request) {
        List<Long> userIds = request.getUserIds();
        userIds.add(user.getId());

        validateTeamSize(userIds);
        validateUserDuplicate(userIds);
        validateUserCombinationExists(userIds);

        Team team = Team.builder()
                .name(request.getName())
                .isVisibleToSameUniversity(request.getIsVisibleToSameUniversity())
                .university(user.getUniversity())
                .teamUsersCombinationHash(TeamUsersCombinationHash.of(userIds))
                .build();

        List<TeamRegion> teamRegions = getTeamRegions(request.getRegionIds(), team);
        List<TeamUser> teamUsers = getTeamUsers(userIds, team);

        teamRegionRepository.saveAll(teamRegions);
        teamUserRepository.saveAll(teamUsers);
        teamRepository.save(team);

        return getTeamResponse(team, teamRegions, teamUsers);
    }


    public TeamResponse readTeam(User user, Long teamId) {
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다."));
        List<TeamUser> teamUsers = teamUserRepository.findAllByTeam(team);
        List<TeamRegion> teamRegions = teamRegionRepository.findAllByTeam(team);

        validateUserIsPartOfTeam(user, teamUsers);

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
        List<Long> userIds = request.getUserIds();
        userIds.add(user.getId());
        validateTeamSize(userIds);
        validateUserDuplicate(userIds);

        List<TeamRegion> teamRegions = teamRegionRepository.findAllByTeamId(teamId);
        List<TeamUser> teamUsers = teamUserRepository.findAllByTeamId(teamId);

        validateUserIsPartOfTeam(user, teamUsers);
        validateAllUserAreSameUniversity(teamUsers.stream()
                        .map(TeamUser::getUser)
                        .collect(Collectors.toList()));
        validateUserCombinationExists(userIds);

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다."));
        team.update(request.getName(), request.getIsVisibleToSameUniversity(), TeamUsersCombinationHash.of(userIds));

        teamUserRepository.deleteAll(teamUsers);
        teamRegionRepository.deleteAll(teamRegions);
        teamRegionRepository.saveAll(getTeamRegions(request.getRegionIds(), team));
        teamUserRepository.saveAll(getTeamUsers(userIds, team));

        List<TeamRegion> updateTeamRegions = teamRegionRepository.findAllByTeamId(teamId);
        List<TeamUser> updateTeamUsers = teamUserRepository.findAllByTeamId(teamId);

        return getTeamResponse(team, updateTeamRegions, updateTeamUsers);
    }

    @Transactional
    public void deleteTeam(User user, Long teamId) {
        validateUserIsPartOfTeam(user, teamUserRepository.findAllByTeamId(teamId));
        teamUserRepository.deleteAllByTeamId(teamId);
        teamRegionRepository.deleteAllByTeamId(teamId);
        teamRepository.deleteById(teamId);
    }

    private TeamResponse getTeamResponse(Team team, List<TeamRegion> teamRegions, List<TeamUser> teamUsers) {

        List<RegionResponse> regionResponses = teamRegions.stream()
                .map(TeamRegion::getRegion)
                .map(regionMapper::toRegionResponse)
                .collect(Collectors.toList());

        List<UserWithUniversityResponse> userWithUniversityResponses = teamUsers.stream()
                .map(TeamUser::getUser)
                .map(this::getUserWithUniversityResponse)
                .collect(Collectors.toList());

        return teamMapper.toTeamResponse(team, regionResponses, userWithUniversityResponses);
    }
    private UserWithUniversityResponse getUserWithUniversityResponse(User user) {
        University university = universityRepository.findById(user.getUniversity().getId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 학교입니다."));

        return userMapper.toUserWithUniversityResponse(userMapper.toUserResponse(user), universityMapper.toUniversityResponse(university));
    }

    private List<TeamUser> getTeamUsers(List<Long> userIds, Team team) {
        List<User> users = userIds.stream()
                .map(userId -> userRepository.findById(userId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다.")))
                .collect(Collectors.toList());

        validateAllUserAreSameUniversity(users);

        return users.stream()
                .map(user -> TeamUser.builder()
                        .team(team)
                        .user(user)
                        .build())
                .collect(Collectors.toList());
    }


    private List<TeamRegion> getTeamRegions(List<Long> regionIds, Team team) {
        return regionIds.stream()
                .map(regionId -> regionRepository.findById(regionId)
                        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 지역입니다.")))
                .map(region -> TeamRegion.builder()
                        .team(team)
                        .region(region)
                        .build())
                .collect(Collectors.toList());
    }

    private void validateAllUserAreSameUniversity(List<User> users) {
        List<Long> universityIds = users.stream()
                .map(User::getUniversity)
                .map(University::getId)
                .collect(Collectors.toList());

        if (universityIds.stream().distinct().count() != 1) {
            throw new IllegalArgumentException("유저들이 모두 같은 학교가 아닙니다.");
        }
    }
    private void validateUserCombinationExists(List<Long> userIds) {
        TeamUsersCombinationHash teamUsersCombinationHash = TeamUsersCombinationHash.of(userIds);

        if (userIds.size() > 1 && teamRepository.findByTeamUsersCombinationHash(teamUsersCombinationHash).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 팀입니다.");
        }
    }
    private void validateTeamSize(List<Long> userIds) {
        if (userIds.size() > 3) {
            throw new IllegalArgumentException("팀원은 최대 3명까지만 가능합니다.");
        }
    }
    private void validateUserDuplicate(List<Long> userIds) {
        if(userIds.size() != userIds.stream().distinct().count()) {
            throw new IllegalArgumentException("중복된 유저가 존재합니다.");
        }
    }
    private void validateUserIsPartOfTeam(User user, List<TeamUser> teamUsers) {

        List<Long> teamUserIds = teamUsers.stream()
                .map(TeamUser::getUser)
                .map(User::getId)
                .collect(Collectors.toList());

        if(!teamUserIds.contains(user.getId())) {
            throw new IllegalArgumentException("팀에 속해있지 않은 유저입니다.");
        }
    }

}
