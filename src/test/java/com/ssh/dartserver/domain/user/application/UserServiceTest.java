package com.ssh.dartserver.domain.user.application;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.domain.chat.domain.ChatRoom;
import com.ssh.dartserver.domain.chat.domain.ChatRoomUser;
import com.ssh.dartserver.domain.chat.infra.ChatRoomRepository;
import com.ssh.dartserver.domain.chat.infra.ChatRoomUserRepository;
import com.ssh.dartserver.domain.friend.domain.Friend;
import com.ssh.dartserver.domain.friend.infra.FriendRepository;
import com.ssh.dartserver.domain.proposal.domain.Proposal;
import com.ssh.dartserver.domain.proposal.domain.ProposalStatus;
import com.ssh.dartserver.domain.proposal.infra.ProposalRepository;
import com.ssh.dartserver.domain.team.domain.Region;
import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamRegion;
import com.ssh.dartserver.domain.team.domain.TeamUser;
import com.ssh.dartserver.domain.team.infra.RegionRepository;
import com.ssh.dartserver.domain.team.infra.TeamRegionRepository;
import com.ssh.dartserver.domain.team.infra.TeamRepository;
import com.ssh.dartserver.domain.team.infra.TeamUserRepository;
import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.university.infra.UniversityRepository;
import com.ssh.dartserver.domain.user.UserTestFixture;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.personalinfo.PersonalInfo;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.testing.IntegrationTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class UserServiceTest extends ApiTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UniversityRepository universityRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private FriendRepository friendRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamUserRepository teamUserRepository;

    @Autowired
    private TeamRegionRepository teamRegionRepository;

    @Autowired
    private ProposalRepository proposalRepository;

    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private ChatRoomUserRepository chatRoomUserRepository;


    @DisplayName("대학 정보와 개인 정보를 받아 회원 가입을 진행한다.")
    @Test
    void signUp_validSignUpRequest_success() {

        //given
        final University university = saveUniversity();
        final PersonalInfo personalInfo = UserTestFixture.getPersonalInfo();
        final User loggedInUser = userRepository.save(UserTestFixture.getLoggedInUser(1L));

        //when
        final User actual = userService.signUp(loggedInUser, personalInfo, university.getId());

        //then
        assertThat(actual).isNotNull();
        assertThat(actual.getUniversity()).usingRecursiveComparison().isEqualTo(university);
        assertThat(actual.getPersonalInfo()).usingRecursiveComparison().isEqualTo(personalInfo);
    }


    @DisplayName("사용자 정보를 조회한다.")
    @Test
    void read_validReadRequest_success() {
        //given
        final University university = saveUniversity();

        final User expected = userRepository.save(UserTestFixture.getSignedUpUser(1L, university));

        //when
        final User actual = userService.read(expected.getId());

        //then
        assertThat(actual).isNotNull();
        assertThat(actual).usingRecursiveComparison()
                .ignoringFields("profileQuestions")
                .ignoringFields("nextVoteAvailableDateTime")
                .isEqualTo(expected);
    }

    @DisplayName("사용자의 닉네임, 프로필 사진을 수정한다.")
    @Test
    void update_validUpdateRequest_success() {
        //given
        final University university = saveUniversity();
        final User user = UserTestFixture.getSignedUpUser(1L, university);

        final String nickname = "수정된 닉네임";
        final String profileImageUrl = "https://profile-image.com/1";

        //when
        final User actual = userService.update(user, nickname, profileImageUrl);

        //then
        assertThat(actual).isNotNull();
        assertThat(actual.getPersonalInfo().getNickname().getValue()).isEqualTo(nickname);
        assertThat(actual.getPersonalInfo().getProfileImageUrl().getValue()).isEqualTo(profileImageUrl);
    }

    /**
     * TODO: 팀에 대한 테스트 코드 추가 작성 필요
     * 2024.07.12
     * 추후 팀 생성 플로우가 변경될 여지가 있어, 변경 후 테스트 코드 작성 예정
     * 팀 관련 데이터 singleTeamFriend, TeamRegion, TeamUser, Team
     * 생성 후 호감 생성
     * 채팅방 열기
     */

    @DisplayName("사용자와 관련 정보들을 삭제한다.")
    @Test
    void delete_validDeleteRequest_success() {
        //given
        final University university = saveUniversity();
        final Region region = saveRegion();

        //사용자 저장
        final User user = UserTestFixture.getSignedUpUser(1L, university);
        final User friendUser = UserTestFixture.getSignedUpUser(2L, university);
        userRepository.saveAll(List.of(user, friendUser));

        //친구 관계 저장
        final Friend friend = saveFriend(user, friendUser);

        //팀 생성
        final Team requestingTeam = saveTeamAndRelatedData(user, university, region, "호감전송팀");
        final Team requestedTeam = saveTeamAndRelatedData(friendUser, university, region, "호감요청받은팀");

        //호감 생성
        final Proposal proposal = saveProposal(requestingTeam, requestedTeam);

        //채팅방
        final ChatRoom chatRoom = saveChatRoom(proposal);

        saveChatRoomUser(user, chatRoom);


        //when
        userService.delete(user);

        //then
        final Optional<User> actualUser = userRepository.findById(user.getId());
        final Optional<Friend> actualFriend = friendRepository.findById(friend.getId());
        assertThat(actualUser).isEmpty();
        assertThat(actualFriend).isEmpty();

    }

    private void saveChatRoomUser(final User user, final ChatRoom chatRoom) {
        final ChatRoomUser chatRoomUser = ChatRoomUser.builder()
                .user(user)
                .chatRoom(chatRoom)
                .build();
        chatRoomUserRepository.save(chatRoomUser);
    }

    private ChatRoom saveChatRoom(final Proposal proposal) {
        final ChatRoom chatRoom = ChatRoom.builder()
                .latestChatMessageContent(null)
                .proposal(proposal)
                .build();

        chatRoomRepository.save(chatRoom);
        return chatRoom;
    }

    private Proposal saveProposal(final Team requestingTeam, final Team requestedTeam) {
        final Proposal proposal = Proposal.builder()
                .proposalStatus(ProposalStatus.PROPOSAL_SUCCESS)
                .requestingTeam(requestingTeam)
                .requestedTeam(requestedTeam)
                .build();

        proposalRepository.save(proposal);
        return proposal;
    }

    private Friend saveFriend(final User user, final User friendUser) {
        return friendRepository.save(Friend.builder()
                .user(user)
                .friendUser(friendUser)
                .build());
    }

    private Team saveTeamAndRelatedData(final User user, final University university, final Region region, final String teamName) {

        final Team team = saveTeam(teamName, university);
        saveTeamUser(user, team);
        saveTeamRegion(region, team);

        return team;
    }

    private Team saveTeam(final String teamName, final University university) {
        return teamRepository.save(Team.builder()
                .name(teamName)
                .university(university)
                .isVisibleToSameUniversity(Boolean.TRUE)
                .build());
    }

    private void saveTeamRegion(final Region region, final Team team) {
        teamRegionRepository.save(TeamRegion.builder()
                .team(team)
                .region(region)
                .build());
    }

    private void saveTeamUser(final User user, final Team team) {
        teamUserRepository.save(
                TeamUser.builder()
                        .user(user)
                        .team(team)
                        .build());
    }

    private University saveUniversity() {
        return universityRepository.save(
                University.builder()
                        .id(1L)
                        .name("서울대학교")
                        .build());
    }

    private Region saveRegion() {
        return regionRepository.save(
                Region.builder()
                        .name("서울")
                        .build()
        );
    }
}