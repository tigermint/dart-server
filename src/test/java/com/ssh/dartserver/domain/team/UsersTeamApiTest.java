package com.ssh.dartserver.domain.team;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.UserManager;
import com.ssh.dartserver.domain.region.RegionSteps;
import com.ssh.dartserver.domain.team.infra.RegionRepository;
import com.ssh.dartserver.domain.university.UniversitySteps;
import com.ssh.dartserver.domain.university.infra.UniversityRepository;
import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import com.ssh.dartserver.testing.IntegrationTest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
public abstract class UsersTeamApiTest extends ApiTest {
    @Autowired
    private UserManager userManager;

    @Autowired
    UniversityRepository universityRepository;

    @Autowired
    RegionRepository regionRepository;

    String jwtToken;

    @BeforeEach
    void setUp() {
        universityRepository.saveAll(UniversitySteps.대학생성요청_생성(3));
        regionRepository.saveAll(RegionSteps.지역생성요청_생성(3));
        jwtToken = userManager.createTestUserWithInformation(Gender.MALE).getJwtToken();
    }

    /**
     * GET /v1/users/me/teams/{teamId}
     * TODO: 팀 조회 테스트에서 상세 값까지 테스트 필요
     */

    @DisplayName("teamId를 통해 내가 만든 팀을 조회하면, 200과 팀 정보를 반환한다.")
    @Test
    void test_readTeam_isSuccess() {
        //given
        final String teamId = getCreatedTeamId();

        //when
        final ExtractableResponse<Response> extractableResponse = RestAssured
                .given().log().all()
                .auth().oauth2(jwtToken)
                .when()
                .get("/v1/users/me/teams/" + teamId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        //then
        assertThat(extractableResponse.body().jsonPath().getString("teamId")).isEqualTo(teamId);
    }


    /**
     * DELETE /v1/users/me/teams/{teamId}
     */
    @DisplayName("teamId를 통해 내가 만든 팀을 삭제하면, 204를 반환한다.")
    @Test
    void test_deleteTeam_NoContent() {
        //given
        final String teamId = getCreatedTeamId();

        //when & then
        RestAssured
                .given().log().all()
                .auth().oauth2(jwtToken)
                .when()
                .delete("/v1/users/me/teams/" + teamId)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    private String getCreatedTeamId() {
        final ExtractableResponse<Response> extractableResponse = TeamSteps.팀_생성_요청(jwtToken, TeamRequestTestFixture.getTeamRequest());
        final String location = extractableResponse.header("Location");
        return location.substring(location.lastIndexOf("/") + 1);
    }
}
