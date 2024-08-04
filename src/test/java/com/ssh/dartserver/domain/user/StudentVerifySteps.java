package com.ssh.dartserver.domain.user;

import com.ssh.dartserver.domain.user.presentation.v1.request.UserStudentIdCardVerificationRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class StudentVerifySteps {
    public static ExtractableResponse<Response> 학생증인증요청(final String jwtToken, final UserStudentIdCardVerificationRequest request) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .auth().oauth2(jwtToken)
            .body(request)
            .when()
            .post("/v1/users/me/verify-student-id-card")
            .then()
            .log().all().extract();
    }

    public static UserStudentIdCardVerificationRequest 학생증인증요청_생성() {
        UserStudentIdCardVerificationRequest request = new UserStudentIdCardVerificationRequest();
        request.setName("학생증이름");
        request.setStudentIdCardImageUrl("https://via.placeholder.com/200x200");

        return request;
    }
}
