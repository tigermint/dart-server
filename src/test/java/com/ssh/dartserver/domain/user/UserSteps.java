package com.ssh.dartserver.domain.user;

import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import com.ssh.dartserver.domain.user.dto.UserSignupRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class UserSteps {
    public static ExtractableResponse<Response> 회원가입요청(final String jwtToken, final UserSignupRequest request) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .auth().oauth2(jwtToken)
            .body(request)
            .when()
            .post("/v1/users/signup")
            .then()
            .log().all().extract();
    }

    public static UserSignupRequest 회원가입요청_생성() {
        return UserSignupRequest.builder()
            .universityId(1L)
            .admissionYear(2010)
            .birthYear(2005)
            .name("테스트")
            .phone("01012345678")
            .gender(Gender.MALE)
            .build();
    }
}
