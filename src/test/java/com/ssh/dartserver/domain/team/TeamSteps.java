package com.ssh.dartserver.domain.team;

import com.ssh.dartserver.domain.team.dto.TeamRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class TeamSteps {
    public static ExtractableResponse<Response> 팀_생성_요청(final String jwtToken, final TeamRequest request) {
        return RestAssured
                .given().log().all()
                .auth().oauth2(jwtToken)
                .contentType("application/json")
                .body(request)
                .when()
                .post("/v1/teams")
                .then().log().all()
                .extract();
    }
}
