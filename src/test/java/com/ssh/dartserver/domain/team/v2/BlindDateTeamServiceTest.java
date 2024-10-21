package com.ssh.dartserver.domain.team.v2;

import static com.ssh.dartserver.domain.university.UniversitySteps.대학생성요청_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.TestRepository;
import com.ssh.dartserver.domain.image.domain.Image;
import com.ssh.dartserver.domain.image.domain.ImageType;
import com.ssh.dartserver.domain.image.infra.ImageRepository;
import com.ssh.dartserver.domain.team.domain.Region;
import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamImage;
import com.ssh.dartserver.domain.team.domain.TeamRegion;
import com.ssh.dartserver.domain.team.domain.vo.TeamDescription;
import com.ssh.dartserver.domain.team.infra.RegionRepository;
import com.ssh.dartserver.domain.team.infra.TeamRegionRepository;
import com.ssh.dartserver.domain.team.infra.TeamRepository;
import com.ssh.dartserver.domain.team.v2.dto.BlindDateTeamInfo;
import com.ssh.dartserver.domain.team.v2.dto.BlindDateTeamSimpleInfo;
import com.ssh.dartserver.domain.team.v2.dto.CreateTeamRequest;
import com.ssh.dartserver.domain.team.v2.dto.UpdateTeamRequest;
import com.ssh.dartserver.domain.university.domain.University;
import com.ssh.dartserver.domain.university.infra.UniversityRepository;
import com.ssh.dartserver.domain.user.domain.AuthInfo;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.personalinfo.BirthYear;
import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import com.ssh.dartserver.domain.user.domain.personalinfo.PersonalInfo;
import com.ssh.dartserver.domain.user.domain.studentverificationinfo.StudentVerificationInfo;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.global.common.Role;
import com.ssh.dartserver.testing.IntegrationTest;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
class BlindDateTeamServiceTest extends ApiTest {

    @Autowired
    private BlindDateTeamService blindDateTeamService;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TeamRegionRepository teamRegionRepository;

    @Autowired
    private TeamImageRepository teamImageRepository;

    @Autowired
    private RegionRepository regionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UniversityRepository universityRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private TestRepository testRepository;


    @BeforeEach
    void setUp() {
        // DB에 지역 등록
        List.of("서울", "제주특별자치도", "NorthKorea123").forEach(
                region -> regionRepository.save(Region.builder().name(region).build())
        );

        List<University> universities = 대학생성요청_생성(3);
        universityRepository.saveAll(universities);
    }

    @Nested
    @DisplayName("팀 생성 테스트")
    class CreateTeam {

        @Test
        @Transactional
        @DisplayName("정상적인 값이 주어질 때 팀을 정상 생성한다.")
        void createTeam_withValidInput_createsTeamSuccessfully() {
            // given
            User user = userRepository.save(User.builder()
                    .authInfo(AuthInfo.of("testUser", "kakao", "12345678"))
                    .personalInfo(PersonalInfo.builder().build())
                    .role(Role.USER)
                    .build());

            // 팀 생성 요청 객체
            CreateTeamRequest request = new CreateTeamRequest(
                    "TeamName",
                    "This is a team description.",
                    true,
                    List.of(1L, 2L),
                    List.of("http://example.com/image1.jpg", "http://example.com/image2.jpg")
            );

            // when
            blindDateTeamService.createTeam(user, request);

            // then
            // 팀이 생성되었는지 확인
            Team createdTeam = teamRepository.findAll().get(0);
            assertThat(createdTeam).isNotNull();
            assertThat(createdTeam.getName().getValue()).isEqualTo("TeamName");
            assertThat(createdTeam.getDescription().getDescription()).isEqualTo("This is a team description.");
            assertThat(createdTeam.getIsVisibleToSameUniversity()).isTrue();

            // 지역이 매핑되었는지 확인
            List<TeamRegion> teamRegions = teamRegionRepository.findAll();
            assertThat(teamRegions).hasSize(2);
            assertThat(teamRegions.get(0).getRegion().getName()).isIn("서울", "제주특별자치도");

            // 이미지가 매핑되었는지 확인
            List<TeamImage> teamImages = teamImageRepository.findAll();
            assertThat(teamImages).hasSize(2);
            assertThat(teamImages.get(0).getImage().getData()).isEqualTo("http://example.com/image1.jpg");
        }

        @Test
        @DisplayName("이미 팀을 만든 사용자가 팀을 생성하려고 할 때, 예외가 발생한다.")
        void createTeam_ShouldThrowException_WhenUserAlreadyHasTeam() {
            // Given: 사용자가 이미 팀을 가지고 있는 상황을 준비합니다.
            User user = createUser();

            Team team = Team.builder()
                    .user(user)
                    .name("OLD TEAM")
                    .description(new TeamDescription("팀 설명입니다."))
                    .isVisibleToSameUniversity(true)
                    .build();
            teamRepository.save(team);

            // When: 사용자가 다시 팀을 만들려고 할 때
            CreateTeamRequest request = new CreateTeamRequest(
                    "TeamName",
                    "This is a team description.",
                    true,
                    List.of(1L, 2L),
                    List.of("http://example.com/image1.jpg", "http://example.com/image2.jpg")
            );

            // Then: IllegalStateException 예외가 발생해야 합니다.
            assertThrows(IllegalStateException.class, () -> {
                blindDateTeamService.createTeam(user, request);
            });
        }

        @Test
        @DisplayName("요청 객체가 null인 경우 예외가 발생한다.")
        void createTeam_ShouldThrowException_WhenRequestIsNull() {
            // Given: 사용자 정보는 있지만 요청 객체가 null인 상황
            User user = createUser();
            CreateTeamRequest request = null;

            // Expect: null인 요청 객체를 사용하여 팀을 만들려고 할 때, IllegalArgumentException 예외가 발생해야 합니다.
            assertThrows(IllegalArgumentException.class, () -> {
                blindDateTeamService.createTeam(user, request);
            });
        }


        @Test
        @DisplayName("사용자 객체가 null인 경우 예외가 발생한다.")
        void createTeam_ShouldThrowException_WhenUserIsNull() {
            // Given: 사용자 정보는 있지만 요청 객체가 null인 상황
            User user = null;

            CreateTeamRequest request = new CreateTeamRequest(
                    "TeamName",
                    "This is a team description.",
                    true,
                    List.of(1L, 2L),
                    List.of("http://example.com/image1.jpg", "http://example.com/image2.jpg")
            );

            // Expect: IllegalArgumentException 예외가 발생해야 합니다.
            assertThrows(IllegalArgumentException.class, () -> {
                blindDateTeamService.createTeam(user, request);
            });
        }

    }

    @Nested
    @DisplayName("팀 상세 조회 테스트")
    class GetTeamInfo {

        @Test
        @DisplayName("등록한 팀의 id를 전달하면 정상적으로 조회한다.")
        @Transactional
        void getTeamInfo_Success_WhenTeamIsCreatedByUser() {
            // Given: 사용자가 팀을 생성한 상황을 준비합니다.
            User user = createUser();
            Team team = Team.builder()
                    .user(user)
                    .name("멋진 TEAM")
                    .description(new TeamDescription("팀 설명입니다."))
                    .isVisibleToSameUniversity(true)
                    .build();

            List<Region> regions = regionRepository.findAllByIdIn(List.of(1L, 2L));
            List<TeamRegion> teamRegions = regions.stream()
                    .map(region -> TeamRegion.builder()
                            .region(region)
                            .team(team)
                            .build())
                    .toList();
            teamRegions = teamRegionRepository.saveAll(teamRegions);

            List<Image> images = List.of("http://example.com/image1.jpg", "http://example.com/image2.jpg").stream()
                    .map(url -> new Image(null, ImageType.URL, url))
                    .toList();
            imageRepository.saveAll(images);
            List<TeamImage> teamImages = images.stream()
                    .map(image -> TeamImage.builder()
                            .team(team)
                            .image(image)
                            .build())
                    .toList();
            teamImageRepository.saveAll(teamImages);

            team.setTeamRegions(teamRegions);
            team.setTeamImages(teamImages);
            teamRepository.save(team);

            // When: 내가 등록한 팀의 id로 팀 정보를 조회할 때
            BlindDateTeamInfo teamInfo = blindDateTeamService.getTeamInfo(team.getId());

            // Then: 팀 정보가 정상적으로 반환되어야 합니다. TODO v2 팀 전체 정보가 잘 들어오는지 확인!
            assertThat(teamInfo).isNotNull();
            assertAll(
                    () -> assertThat(teamInfo.id()).isEqualTo(team.getId()),
                    () -> assertThat(teamInfo.leaderId()).isEqualTo(user.getId()),
                    () -> assertThat(teamInfo.name()).isEqualTo("멋진 TEAM"),
                    () -> assertThat(teamInfo.description()).isEqualTo("팀 설명입니다."),
                    () -> assertThat(teamInfo.isVisibleToSameUniversity()).isTrue(),
                    () -> assertThat(teamInfo.age()).isEqualTo(25),  // 나이 값 설정
                    () -> assertThat(teamInfo.isCertified()).isFalse(),
                    () -> assertThat(teamInfo.universityName()).isEqualTo("Tech University 1000"),
                    () -> assertThat(teamInfo.departmentName()).isEqualTo("컴퓨터공학과"),
                    () -> assertThat(teamInfo.regions())
                            .extracting("name")
                            .containsExactlyInAnyOrder("서울", "제주특별자치도"),
                    () -> assertThat(teamInfo.imageUrls())
                            .containsExactly("http://example.com/image1.jpg", "http://example.com/image2.jpg"),
                    () -> assertThat(teamInfo.isAlreadyProposalTeam()).isFalse()
            );
        }

        @Test
        @DisplayName("등록되지 않은 TeamId를 전달하면 예외가 발생한다.")
        void getTeamInfo_ShouldThrowException_WhenTeamIdIsNotRegistered() {
            // Given: 등록되지 않은 팀 id를 사용하여 조회 시도
            long invalidTeamId = 999L;

            // Expect: IllegalArgumentException 예외가 발생해야 합니다.
            assertThrows(IllegalArgumentException.class, () -> {
                blindDateTeamService.getTeamInfo(invalidTeamId);
            });
        }

        @Test
        @DisplayName("TeamId로 0 또는 음수가 주어지면 예외가 발생한다.")
        void getTeamInfo_ShouldThrowException_WhenTeamIdIsZeroOrNegative() {
            // Given: 0 또는 음수 팀 id를 사용하여 조회 시도
            long zeroTeamId = 0L;
            long negativeTeamId = -1L;

            // Expect: IllegalArgumentException 예외가 발생해야 합니다.
            assertThrows(IllegalArgumentException.class, () -> {
                blindDateTeamService.getTeamInfo(zeroTeamId);
            });

            assertThrows(IllegalArgumentException.class, () -> {
                blindDateTeamService.getTeamInfo(negativeTeamId);
            });
        }

        @Disabled
        @Test
        @DisplayName("v1 팀의 id를 전달하면 정상적으로 조회한다.")
        void getTeamInfo_Success_WhenTeamIdIsValid() {
            throw new UnsupportedOperationException();  // TODO 기능 구현 필요 + 추가로 v1 클라이언트의 요청도 커버해야함 (이건 기존 서비스 테스트)
        }

    }

    @Nested
    @DisplayName("팀 목록 조회 테스트")
    class GetTeamList {

        @Test
        @DisplayName("인자로 전달된 User가 null인 경우 예외가 발생한다.")
        void shouldThrowExceptionWhenUserIsNull() {
            // given
            Pageable pageable = PageRequest.of(0, 10);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                blindDateTeamService.getTeamList(null, pageable);
            });
        }

        @Test
        @DisplayName("인자로 전달된 Pageable이 null인 경우 예외가 발생한다.")
        @Transactional
        void shouldThrowExceptionWhenPageableIsNull() {
            // given
            University university = testRepository.addUniversity("Test Univ", "CS");
            User user = testRepository.createUser("Test User", Gender.MALE, university);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                blindDateTeamService.getTeamList(user, null);
            });
        }

        @Test
        @DisplayName("전달된 인자로 조회한 값이 없는 경우 성공적으로 반환한다.")
        @Transactional
        void shouldReturnNoneTeamsSuccessfully() {
            // given
            testRepository.addRegion("부산");
            testRepository.addRegion("인천");
            University university = testRepository.addUniversity("Test Univ", "CS");

            User user = testRepository.createUser("Male User", Gender.MALE, university);
            User user2 = testRepository.createUser("Male User2", Gender.MALE, university);

            Pageable pageable = PageRequest.of(0, 10);
            testRepository.createTeam("Team 1", user, Boolean.TRUE, "부산");
            testRepository.createTeam("Team 2", user2, Boolean.TRUE, "인천");

            // when
            Page<BlindDateTeamSimpleInfo> teamList = blindDateTeamService.getTeamList(user, pageable);

            // then
            assertNotNull(teamList);
            assertThat(teamList.getTotalElements()).isZero();
        }

        @Test
        @DisplayName("전달된 인자로 조회한 값이 한 개인 경우 성공적으로 반환한다.")
        @Transactional
        void shouldReturnSingleTeamSuccessfully() {
            // given
            testRepository.addRegion("부산");
            testRepository.addRegion("인천");
            University university = testRepository.addUniversity("Test Univ", "CS");

            User user = testRepository.createUser("Male User", Gender.MALE, university);
            User female = testRepository.createUser("Female User", Gender.FEMALE, university);

            Pageable pageable = PageRequest.of(0, 10);
            testRepository.createTeam("Team 1", user, Boolean.TRUE, "부산");
            testRepository.createTeam("Team 2", female, Boolean.TRUE, "인천");

            // when
            Page<BlindDateTeamSimpleInfo> teamList = blindDateTeamService.getTeamList(user, pageable);

            // then
            assertNotNull(teamList);
            BlindDateTeamSimpleInfo teamInfo = teamList.getContent().get(0);
            assertAll(
                    () -> assertThat(teamList.getTotalElements()).isEqualTo(1),
                    () -> assertThat(teamInfo.id()).isEqualTo(2),
                    () -> assertThat(teamInfo.leaderId()).isEqualTo(2),
                    () -> assertThat(teamInfo.age()).isEqualTo(25),
                    () -> assertThat(teamInfo.isCertified()).isFalse(),
                    () -> assertThat(teamInfo.universityName()).isEqualTo("Test Univ"),
                    () -> assertThat(teamInfo.departmentName()).isEqualTo("CS"),
                    () -> assertThat(teamInfo.name()).isEqualTo("Team 2"),
                    () -> assertThat(teamInfo.description()).isEqualTo("팀 설명입니다."),
                    () -> assertThat(teamInfo.isVisibleToSameUniversity()).isTrue(),
                    () -> assertThat(teamInfo.regions().get(0).getName()).isEqualTo("인천"),
                    () -> assertThat(teamInfo.imageUrls().get(0)).isEqualTo("https://www.naver.com/image1.jpg"),
                    () -> assertThat(teamInfo.isAlreadyProposalTeam()).isFalse()
            );
        }

        @Test
        @DisplayName("전달된 인자로 조회한 값이 여러개인 경우 성공적으로 반환한다.")
        @Transactional
        void shouldReturnMultipleTeamsSuccessfully() {

            // given
            testRepository.addRegion("부산");
            testRepository.addRegion("인천");
            University university = testRepository.addUniversity("Test Univ", "CS");

            User user = testRepository.createUser("Male User", Gender.MALE, university);
            User female1 = testRepository.createUser("Female User1", Gender.FEMALE, university);
            User female2 = testRepository.createUser("Female User2", Gender.FEMALE, university);
            User female3 = testRepository.createUser("Female User3", Gender.FEMALE, university);
            User female4 = testRepository.createUser("Female User4", Gender.FEMALE, university);
            User female5 = testRepository.createUser("Female User5", Gender.FEMALE, university);
            User female6 = testRepository.createUser("Female User6", Gender.FEMALE, university);

            Pageable pageable = PageRequest.of(0, 10);
            testRepository.createTeam("Team", user, Boolean.TRUE, "부산");
            testRepository.createTeam("True1", female1, Boolean.TRUE, "인천");
            testRepository.createTeam("False1", female2, Boolean.FALSE, "인천");  // 같은 학교 조회 x
            testRepository.createTeam("False2", female3, Boolean.FALSE, "부산");  // 같은 학교 조회 x
            testRepository.createTeam("True2", female4, Boolean.TRUE, "부산");
            testRepository.createTeam("True3", female5, Boolean.TRUE, "인천");
            testRepository.createTeam("True4", female6, Boolean.TRUE, "인천");

            // when
            Page<BlindDateTeamSimpleInfo> teamList = blindDateTeamService.getTeamList(user, pageable);

            // then
            assertNotNull(teamList);
            assertThat(teamList.getTotalElements()).isEqualTo(4);
        }

        // TODO 조회수 처리 및 푸시 알림?

    }

    @Nested
    @DisplayName("팀 삭제 테스트")
    class DeleteTeam {

        @Test
        @DisplayName("인자로 전달된 User가 null인 경우 예외가 발생한다.")
        void shouldThrowExceptionWhenUserIsNull() {
            // given
            long teamId = 1L;

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                blindDateTeamService.deleteTeam(null, teamId);
            });
        }

        @Test
        @DisplayName("인자로 전달된 teamId에 해당하는 팀이 없을 경우 성공적으로 수행된다.")
        @Transactional
        void shouldSucceedWhenTeamDoesNotExist() {
            // given
            University university = testRepository.addUniversity("Test Univ", "CS");
            User user = testRepository.createUser("Test User", Gender.MALE, university);
            long nonExistingTeamId = 999L;

            // expect
            assertThatNoException().isThrownBy(
                    () -> blindDateTeamService.deleteTeam(user, nonExistingTeamId)
            );
        }

        @Test
        @DisplayName("자신이 만든 팀을 삭제하려는 경우 성공적으로 수행된다.")
        @Transactional
        void shouldSucceedWhenDeleteMyTeam() {
            // given
            testRepository.addRegion("부산");
            University university = testRepository.addUniversity("Test Univ", "CS");
            User user = testRepository.createUser("Test User", Gender.MALE, university);
            Team team = testRepository.createTeam("Team 1", user, Boolean.TRUE, "부산");

            long teamId = team.getId();
            long imageId = testRepository.findTeamImagesByTeamId(teamId).get(0).getImage().getId();

            // when
            blindDateTeamService.deleteTeam(user, team.getId());

            // then
            assertAll(
                    () -> assertThat(teamRepository.findAll()).isEmpty(),
                    // 팀 삭제 후에도 user는 여전히 존재해야한다.
                    () -> assertThat(userRepository.existsById(user.getId())).isTrue(),
                    // 팀 삭제 시 TeamRegions는 함께 삭제된다.
                    () -> assertThat(testRepository.findTeamRegionsByTeamId(teamId)).isEmpty(),
                    // 팀 삭제 시 TeamImages는 함께 삭제된다.
                    () -> assertThat(testRepository.findTeamImagesByTeamId(teamId)).isEmpty(),
                    // 팀 삭제 시 TeamImages와 연결된 Image도 함께 삭제된다.
                    () -> assertThat(testRepository.findImageByImageId(imageId)).isEmpty()
            );
        }

        @Test
        @DisplayName("이미 삭제된 팀을 삭제하려는 경우 성공적으로 수행된다.")
        @Transactional
        void shouldSucceedWhenTeamAlreadyDeleted() {
            // given
            testRepository.addRegion("부산");
            University university = testRepository.addUniversity("Test Univ", "CS");
            User user = testRepository.createUser("Test User", Gender.MALE, university);
            Team team = testRepository.createTeam("Team 1", user, Boolean.TRUE, "부산");

            // expect
            assertThatNoException().isThrownBy(
                    () -> {
                        blindDateTeamService.deleteTeam(user, team.getId());
                        blindDateTeamService.deleteTeam(user, team.getId());  // 삭제된 팀을 다시한번 삭제
                    }
            );
        }

        @Test
        @DisplayName("삭제하려는 팀이 자신이 만든 팀이 아닌 경우 예외가 발생한다.")
        @Transactional
        void shouldThrowExceptionWhenUserIsNotLeader() {
            // given
            testRepository.addRegion("부산");
            University university = testRepository.addUniversity("Test Univ", "CS");
            User leader = testRepository.createUser("Leader", Gender.MALE, university);
            User otherUser = testRepository.createUser("Other User", Gender.MALE, university);
            Team team = testRepository.createTeam("Team 1", leader, Boolean.TRUE, "부산");

            // expect
            assertThrows(IllegalArgumentException.class, () -> {
                blindDateTeamService.deleteTeam(otherUser, team.getId());
            });
        }

        // TODO v1 팀 삭제 로직 처리

    }

    @Nested
    @DisplayName("팀 수정 테스트")
    class UpdateTeam {

        @Test
        @DisplayName("User가 null인 경우 예외가 발생한다.")
        void shouldThrowExceptionWhenUserIsNull() {
            // given
            UpdateTeamRequest request = new UpdateTeamRequest(
                    1L, "새 팀 이름", "새 팀 설명", true, List.of(1L, 2L), List.of("https://example.com/image1.jpg")
            );

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                blindDateTeamService.updateTeam(null, request);
            });
        }

        @Test
        @DisplayName("UpdateTeamRequest가 null인 경우 예외가 발생한다.")
        @Transactional
        void shouldThrowExceptionWhenRequestIsNull() {
            // given
            University university = testRepository.addUniversity("Test Univ", "CS");
            User user = testRepository.createUser("Test User", Gender.MALE, university);

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                blindDateTeamService.updateTeam(user, null);
            });
        }

        @Test
        @DisplayName("팀을 찾을 수 없는 경우 예외가 발생한다.")
        @Transactional
        void shouldThrowExceptionWhenTeamNotFound() {
            // given
            testRepository.addRegion("부산");
            University university = testRepository.addUniversity("Test Univ", "CS");
            User user = testRepository.createUser("Male User", Gender.MALE, university);
            testRepository.createTeam("Team", user, Boolean.TRUE, "부산");

            UpdateTeamRequest request = new UpdateTeamRequest(
                    999L, "새 팀 이름", "새 팀 설명", true, List.of(1L, 2L), List.of("https://example.com/image1.jpg")
            );

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                blindDateTeamService.updateTeam(user, request);
            });
        }


        @Test
        @DisplayName("팀 리더가 아닌 사용자가 팀을 수정하려는 경우 예외가 발생한다.")
        @Transactional
        void shouldThrowExceptionWhenUserIsNotLeader() {
            // given
            testRepository.addRegion("부산");
            University university = testRepository.addUniversity("Test Univ", "CS");
            User leader = testRepository.createUser("Leader", Gender.MALE, university);
            User nonLeader = testRepository.createUser("Non-Leader", Gender.MALE, university);

            Team team = testRepository.createTeam("Team", leader, Boolean.TRUE, "부산");

            UpdateTeamRequest request = new UpdateTeamRequest(
                    team.getId(), "새 팀 이름", "새 팀 설명", true, List.of(1L, 2L), List.of("https://example.com/image1.jpg")
            );

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                blindDateTeamService.updateTeam(nonLeader, request);
            });
        }

        @Test
        @DisplayName("존재하지 않는 RegionId가 있는 경우 예외가 발생한다.")
        @Transactional
        void shouldThrowExceptionWhenRegionNotFound() {
            // given
            testRepository.addRegion("부산");
            University university = testRepository.addUniversity("Test Univ", "CS");
            User user = testRepository.createUser("Test User", Gender.MALE, university);

            Team team = testRepository.createTeam("Team", user, Boolean.TRUE, "부산");

            UpdateTeamRequest request = new UpdateTeamRequest(
                    team.getId(), "새 팀 이름", "새 팀 설명", true, List.of(999L), List.of("https://example.com/image1.jpg")
            );

            // when & then
            assertThrows(IllegalArgumentException.class, () -> {
                blindDateTeamService.updateTeam(user, request);
            });
        }

        @Test
        @DisplayName("팀 수정이 성공적으로 완료된다.")
        @Transactional
        void shouldUpdateTeamSuccessfully() {
            // given
            testRepository.addRegion("부산");
            testRepository.addRegion("인천");
            testRepository.addRegion("천안");
            University university = testRepository.addUniversity("Test Univ", "CS");
            User user = testRepository.createUser("Test User", Gender.MALE, university);

            Team team = testRepository.createTeam("Team", user, Boolean.TRUE, "부산");

            UpdateTeamRequest request = new UpdateTeamRequest(
                    team.getId(), "새 팀 이름", "새 팀 설명", true, new ArrayList<>(List.of(2L, 3L)), new ArrayList<>(List.of("https://example.com/image1.jpg", "https://example.com/image2.jpg"))
            );

            // when
            blindDateTeamService.updateTeam(user, request);

            // then
            assertNotNull(team);
            assertAll(
                    () -> assertThat(team.getName().getValue()).isEqualTo("새 팀 이름"),
                    () -> assertThat(team.getDescription().getDescription()).isEqualTo("새 팀 설명"),
                    () -> assertThat(team.getTeamRegions()).hasSize(2),
                    () -> assertThat(team.getTeamImages()).hasSize(2),
                    () -> assertThat(team.getTeamImages().get(0).getImage().getData()).isIn("https://example.com/image1.jpg", "https://example.com/image2.jpg")
            );

            // TODO 사용되지 않는 TeamRegion이 정상적으로 삭제되는지, TeamImages와 TeamImage가 정상적으로 삭제되는지 확인
        }

    }

    private User createUser() {
        return userRepository.save(User.builder()
                .authInfo(AuthInfo.of("testUser", "kakao", "12345678"))
                .personalInfo(PersonalInfo.builder()
                        .birthYear(BirthYear.from(2000))
                        .gender(Gender.MALE)
                        .build())
                .university(universityRepository.findById(1L).get())
                .studentVerificationInfo(StudentVerificationInfo.newInstance())
                .role(Role.USER)
                .build());
    }

}
