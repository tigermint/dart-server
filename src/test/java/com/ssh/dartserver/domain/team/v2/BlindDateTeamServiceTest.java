package com.ssh.dartserver.domain.team.v2;

import static com.ssh.dartserver.domain.university.UniversitySteps.대학생성요청_생성;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.ssh.dartserver.ApiTest;
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
import com.ssh.dartserver.domain.team.v2.dto.CreateTeamRequest;
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
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
    public class CreateTeam {

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
    public class GetTeamInfo {

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
