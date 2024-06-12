package com.ssh.dartserver.domain.team;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.UserManager;
import com.ssh.dartserver.domain.region.RegionSteps;
import com.ssh.dartserver.domain.team.dto.TeamRequest;
import com.ssh.dartserver.domain.team.infra.RegionRepository;
import com.ssh.dartserver.domain.university.UniversitySteps;
import com.ssh.dartserver.domain.university.infra.UniversityRepository;
import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import com.ssh.dartserver.testing.IntegrationTest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class TeamApiTest extends ApiTest {

    @Autowired
    private UserManager userManager;

    @Autowired
    private UniversityRepository universityRepository;

    @Autowired
    private RegionRepository regionRepository;

    String jwtToken;

    @BeforeEach
    void setUp() {
        universityRepository.saveAll(UniversitySteps.대학생성요청_생성(3));
        regionRepository.saveAll(RegionSteps.지역생성요청_생성(3));
        jwtToken = userManager.createTestUserWithInformation(Gender.MALE).getJwtToken();
    }

    @Nested
    class UsersTest extends UsersTeamApiTest {
    }

    /**
     * POST /v1/teams
     */
    @DisplayName("팀을 생성하면, 201과 URI 주소를 반환한다.")
    @Test
    void test_createTeam_created() {
        //given
        final TeamRequest request = TeamRequestTestFixture.getTeamRequest();

        //when
        final ExtractableResponse<Response> extractableResponse = TeamSteps.팀_생성_요청(jwtToken, request);

        //then
        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());

    }

    /**
     * GET /v1/teams/count
     * TODO: Reponse Json 형태로 반환되도록 수정, Response Convention 정리
     */
    @DisplayName("팀의 수를 조회하면, 200과 현재 팀의 수 * 2 + 50을 반환한다.")
    @Test
    void test_countAllTeams_isSuccess() {
        //given
        final int count = 3;

        for (int i = 0; i < count; i++) {
            final String otherUserJwtToken = userManager.createTestUserWithInformation().getJwtToken();
            TeamSteps.팀_생성_요청(otherUserJwtToken, TeamRequestTestFixture.getTeamRequest());
        }

        //when & then
        RestAssured
                .given().log().all()
                .auth().oauth2(jwtToken)
                .when()
                .get("/v1/teams/count")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body(Matchers.equalTo(String.valueOf(count * 2 + 50)));
    }


    /**
     * GET /v1/teams/{teamId}
     */
    @DisplayName("teamId로 팀을 조회하면, 200과 팀 정보를 반환한다.")
    @Test
    void test_readTeam_isSuccess() {
        //given
        final ExtractableResponse<Response> extractableResponse = TeamSteps.팀_생성_요청(jwtToken, TeamRequestTestFixture.getTeamRequest());
        final String location = extractableResponse.header("Location");
        final String teamId = location.substring(location.lastIndexOf("/") + 1);

        //when
        RestAssured
                .given().log().all()
                .auth().oauth2(jwtToken)
                .when()
                .get("/v1/teams/{teamId}", teamId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("id", Matchers.equalTo(Integer.parseInt(teamId)));

    }

    /**
     * GET /v1/teams
     * Pageable, Condition 없이 Default 요청
     */
    @DisplayName("팀 목록을 조회하면, 200과 이성 팀 목록을 반환한다.")
    @Test
    void test_listTeam_isSuccess() {
        //given
        final int count = 3;

        final HashMap<String, TeamRequest> createdTeamRequestMap = new HashMap<>();

        for (int i = 0; i < count; i++) {
            final String otherUserJwtToken = userManager.createTestUserWithInformation(Gender.FEMALE).getJwtToken();
            final TeamRequest teamRequest = TeamRequestTestFixture.getTeamRequest();
            final ExtractableResponse<Response> extractableResponse = TeamSteps.팀_생성_요청(otherUserJwtToken, teamRequest);

            final String location = extractableResponse.header("Location");
            final String teamId = location.substring(location.lastIndexOf("/") + 1);

            createdTeamRequestMap.put(teamId, teamRequest);
        }

        //when
        final ExtractableResponse<Response> extractableResponse = RestAssured
                .given().log().all()
                .auth().oauth2(jwtToken)
                .when()
                .get("/v1/teams")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();


        //then
        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.OK.value());

        final List<Map<String, Object>> teams = extractableResponse.jsonPath().getList("content");
        assertThat(teams).hasSize(count);

        // 각 팀의 정보가 제대로 들어있는지 검증
        for (Map<String, Object> team : teams) {
            String teamId = String.valueOf(team.get("id"));
            TeamRequest expectedTeam = createdTeamRequestMap.get(teamId);

            assertThat(team).containsEntry("name", expectedTeam.getName());
        }
    }
}
