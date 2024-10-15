package com.ssh.dartserver.domain.team.application;

import com.ssh.dartserver.domain.chat.domain.ChatRoomUser;
import com.ssh.dartserver.domain.chat.infra.ChatRoomUserRepository;
import com.ssh.dartserver.domain.proposal.domain.Proposal;
import com.ssh.dartserver.domain.proposal.infra.ProposalRepository;
import com.ssh.dartserver.domain.question.application.QuestionMapper;
import com.ssh.dartserver.domain.team.domain.*;
import com.ssh.dartserver.domain.team.presentation.response.RegionResponse;
import com.ssh.dartserver.domain.team.presentation.request.TeamRequest;
import com.ssh.dartserver.domain.team.presentation.response.TeamResponse;
import com.ssh.dartserver.domain.team.infra.*;
import com.ssh.dartserver.domain.team.util.RegionMapper;
import com.ssh.dartserver.domain.team.util.TeamMapper;
import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.university.application.UniversityMapper;
import com.ssh.dartserver.domain.university.infra.UniversityRepository;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.profilequestions.ProfileQuestions;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.domain.user.presentation.v1.response.UserProfileResponse;
import com.ssh.dartserver.domain.user.application.ProfileQuestionMapper;
import com.ssh.dartserver.domain.user.application.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    private final SingleTeamFriendRepository singleTeamFriendRepository;
    private final ProposalRepository proposalRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;

    private final UserMapper userMapper;
    private final TeamMapper teamMapper;
    private final RegionMapper regionMapper;
    private final UniversityMapper universityMapper;
    private final ProfileQuestionMapper profileQuestionMapper;
    private final QuestionMapper questionMapper;

    @Transactional
    public Long createSingleTeam(User user, TeamRequest request) {
        List<Long> userIds = request.getUserIds();
        List<Long> regionIds = request.getRegionIds();
        userIds.add(user.getId());

        List<User> users = userRepository.findAllByIdIn(userIds);
        List<Region> regions = regionRepository.findAllByIdIn(regionIds);

        validateUserWithoutTeam(user);

        Team team = Team.builder()
                .name(request.getName())
                .isVisibleToSameUniversity(request.getIsVisibleToSameUniversity())
                .university(user.getUniversity())
                .teamUsersCombinationHash(TeamUsersCombinationHash.of(userIds))
                .build();


        // TODO request의 SingleTeamFriends NPE 발생가능함
        List<SingleTeamFriend> singleTeamFriends = request.getSingleTeamFriends().stream()
                .map(singleTeamFriendDto -> {
                    University university = universityRepository.findById(singleTeamFriendDto.getUniversityId())
                            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 대학교입니다."));
                    return SingleTeamFriend.builder()
                            .nickname(singleTeamFriendDto.getNickname())
                            .birthYear(singleTeamFriendDto.getBirthYear())
                            .profileImageUrl(singleTeamFriendDto.getProfileImageUrl())
                            .university(university)
                            .team(team)
                            .build();
                })
                .collect(Collectors.toList());

        Team savedTeam = teamRepository.save(team);
        singleTeamFriendRepository.saveAll(singleTeamFriends);

        List<TeamRegion> teamRegions = getTeamRegions(regionIds, regions, savedTeam);
        List<TeamUser> teamUsers = getTeamUsers(userIds, users, savedTeam);
        teamRegionRepository.saveAll(teamRegions);
        teamUserRepository.saveAll(teamUsers);
        return savedTeam.getId();
    }

    @Transactional
    public Long createMultipleTeam(User user, TeamRequest request) {
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

        List<TeamUser> teamUsers = teamUserRepository.findAllByTeamId(teamId);
        List<TeamRegion> teamRegions = teamRegionRepository.findAllByTeamId(teamId);
        Team team = teamUsers.stream()
                .map(TeamUser::getTeam)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 팀입니다."));

        validateTeamUserIsPartOfTeam(user, teamUsers);

        return getTeamResponse(team, teamRegions, teamUsers);
    }

    public List<TeamResponse> listTeam(User user) {
        List<Team> myTeams = teamRepository.findAllTeamByUserIdPattern("%-" + user.getId() + "-%");

        Map<Team, List<TeamUser>> teamUsersMap = teamUserRepository.findAllByTeamIn(myTeams).stream()
                .collect(Collectors.groupingBy(TeamUser::getTeam));
        Map<Team, List<TeamRegion>> teamRegionsMap = teamRegionRepository.findAllByTeamIn(myTeams).stream()
                .collect(Collectors.groupingBy(TeamRegion::getTeam));

        return myTeams.stream()
                .map(myTeam -> {
                    List<TeamUser> teamUsers = teamUsersMap.getOrDefault(myTeam, Collections.emptyList());
                    List<TeamRegion> teamRegions =  teamRegionsMap.getOrDefault(myTeam, Collections.emptyList());
                    return getTeamResponse(myTeam, teamRegions, teamUsers);
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

        //내가 현재 팀에 속해 있는지 validation
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
        //teamId로 해당 팀의 TeamUser를 모두 찾는다
        List<TeamUser> teamUsers = teamUserRepository.findAllByTeamId(teamId);


        //User가 해당 팀의 멤버인지 확인
        validateTeamUserIsPartOfTeam(user, teamUsers);

        //TeamUser들의 실제 UserID를 모두 찾는다
        List<Long> teamUserIds = teamUsers.stream()
                .map(TeamUser::getUser)
                .map(User::getId)
                .collect(Collectors.toList());

        //삭제된 팀에 속한 채팅방 유저들을 모두 찾아 삭제
        List<ChatRoomUser> chatRoomUsersInTeamUsers = chatRoomUserRepository.findAllByTeamId(teamId).stream()
                .filter(chatRoomUser -> teamUserIds.contains(chatRoomUser.getUser().getId()))
                .collect(Collectors.toList());
        chatRoomUserRepository.deleteAll(chatRoomUsersInTeamUsers);

        //팀이 보낸 호감을 찾아 null 처리
        List<Proposal> proposalsOfTeam = proposalRepository.findAllByRequestingTeamIdOrRequestedTeamId(teamId, teamId);
        proposalsOfTeam.forEach(proposal -> proposal.updateProposalOnTeamDeletion(teamId));

        //팀과 관련된 정보 제거 -> 왜 이거 다해놨지? Cascade 되는거 아닌가?
        singleTeamFriendRepository.deleteAllByTeamId(teamId);
        teamUserRepository.deleteAllByTeamId(teamId);
        teamRegionRepository.deleteAllByTeamId(teamId);
        teamRepository.deleteById(teamId);
    }

    private TeamResponse getTeamResponse(Team team, List<TeamRegion> teamRegions, List<TeamUser> teamUsers) {
        List<RegionResponse> regionResponses = teamRegions.stream()
                .map(TeamRegion::getRegion)
                .map(regionMapper::toRegionResponse)
                .collect(Collectors.toList());

        return teamMapper.toTeamResponse(
                team,
                regionResponses,
                Optional.of(teamUsers)
                        .filter(users -> users.size() == 1)
                        .map(users -> getSingleTeamUserProfileResponse(team, teamUsers))
                        .orElseGet(() -> getMultipleTeamUserProfileResponse(teamUsers))
        );
    }

    private List<UserProfileResponse> getSingleTeamUserProfileResponse(Team team, List<TeamUser> teamUsers) {
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
                .map(this::getUserProfileResponse)
                .collect(Collectors.toList());
    }

    private List<UserProfileResponse> getMultipleTeamUserProfileResponse(List<TeamUser> teamUsers) {
        return teamUsers.stream()
                .map(TeamUser::getUser)
                .map(this::getUserProfileResponse)
                .collect(Collectors.toList());
    }

    private UserProfileResponse getUserProfileResponse(User user) {
        return userMapper.toUserProfileResponse(
                userMapper.toUserResponse(user),
                universityMapper.toUniversityResponse(user.getUniversity()),
                Optional.ofNullable(user.getProfileQuestions())
                        .map(ProfileQuestions::getValues)
                        .orElse(Collections.emptyList())
                        .stream()
                        .map(profileQuestion ->
                                profileQuestionMapper.toProfileQuestionResponse(
                                        questionMapper.toQuestionResponse(profileQuestion.getQuestion()),
                                        profileQuestion.getCount()
                                ))
                        .collect(Collectors.toList())
        );
    }

    private List<TeamUser> getTeamUsers(List<Long> userIds, List<User> users, Team team) {
        if (users.size() != userIds.size()) {
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
        if (regions.size() != regionIds.size()) {
            throw new IllegalArgumentException("존재하지 않는 지역이 있습니다.");
        }
        return regions.stream()
                .map(region -> TeamRegion.builder()
                        .team(team)
                        .region(region)
                        .build())
                .collect(Collectors.toList());
    }

    private void validateUserWithoutTeam(User user) {
        String userIdPattern = "%-" + user.getId() + "-%";
        if (!teamRepository.findAllTeamByUserIdPattern(userIdPattern).isEmpty()) {
            throw new IllegalArgumentException("이미 팀이 존재하는 사용자입니다.");
        }
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
        if (userIds.size() != userIds.stream().distinct().count()) {
            throw new IllegalArgumentException("중복된 유저가 존재합니다.");
        }
    }

    private void validateTeamUserIsPartOfTeam(User user, List<TeamUser> teamUsers) {
        List<Long> teamUserIds = teamUsers.stream()
                .map(TeamUser::getUser)
                .map(User::getId)
                .collect(Collectors.toList());

        if (!teamUserIds.contains(user.getId())) {
            throw new IllegalArgumentException("팀에 속해있지 않은 유저입니다.");
        }
    }

    private void validateTeamUserAreSameGender(List<User> users) {
        long count = users.stream()
                .map(user -> user.getPersonalInfo().getGender())
                .distinct()
                .count();

        if (count != 1) {
            throw new IllegalArgumentException("팀원들의 성별이 모두 같지 않습니다.");
        }
    }
}
