package com.ssh.dartserver.domain.team.v2;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.domain.team.domain.Region;
import com.ssh.dartserver.domain.team.domain.Team;
import com.ssh.dartserver.domain.team.domain.TeamImage;
import com.ssh.dartserver.domain.team.domain.TeamRegion;
import com.ssh.dartserver.domain.team.domain.vo.TeamDescription;
import com.ssh.dartserver.domain.team.infra.RegionRepository;
import com.ssh.dartserver.domain.team.infra.TeamRegionRepository;
import com.ssh.dartserver.domain.team.infra.TeamRepository;
import com.ssh.dartserver.domain.team.v2.dto.CreateTeamRequest;
import com.ssh.dartserver.domain.user.domain.AuthInfo;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.personalinfo.PersonalInfo;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.global.common.Role;
import com.ssh.dartserver.testing.IntegrationTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
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

    @BeforeEach
    void setUp() {
        // DB에 지역 등록
        List.of("서울", "제주특별자치도", "NorthKorea123").forEach(
                region -> regionRepository.save(Region.builder().name(region).build())
        );
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

    private User createUser() {
        return userRepository.save(User.builder()
                .authInfo(AuthInfo.of("testUser", "kakao", "12345678"))
                .personalInfo(PersonalInfo.builder().build())
                .role(Role.USER)
                .build());
    }

}
