package com.ssh.dartserver.domain.user;

import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import com.ssh.dartserver.domain.user.presentation.v1.request.UserSignUpRequest;
import com.ssh.dartserver.domain.user.presentation.v1.request.UserUpdateRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.ArrayList;
import org.springframework.http.MediaType;

public class UserSteps {
    public static ExtractableResponse<Response> 회원가입요청(final String jwtToken, final UserSignUpRequest request) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .auth().oauth2(jwtToken)
            .body(request)
            .when()
            .post("/v1/users/signup")
            .then()
            .log().all().extract();
    }

    public static UserSignUpRequest 회원가입요청_생성() {
        return UserSignUpRequest.builder()
            .universityId(1L)
            .admissionYear(2010)
            .birthYear(2005)
            .name("테스트")
            .phone("01012345678")
            .gender(Gender.MALE)
            .build();
    }

    public static ExtractableResponse<Response> 내정보확인요청(final String jwtToken) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .auth().oauth2(jwtToken)
            .when()
            .get("/v1/users/me")
            .then()
            .log().all().extract();
    }

    public static ExtractableResponse<Response> 회원탈퇴요청(final String jwtToken) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .auth().oauth2(jwtToken)
            .when()
            .delete("/v1/users/me")
            .then()
            .log().all().extract();
    }

    public static ExtractableResponse<Response> 내정보수정요청(final String jwtToken, UserUpdateRequest request) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .auth().oauth2(jwtToken)
            .body(request)
            .when()
            .patch("/v1/users/me")
            .then()
            .log().all().extract();
    }

    public static UserUpdateRequest 내정보수정요청_생성() {
        UserUpdateRequest request = new UserUpdateRequest();
        request.setNickname("수정된닉네임");
        request.setProfileImageUrl("https://via.placeholder.com/200x200");
        request.setProfileQuestionIds(new ArrayList<>());  // 필요없는걸 빈 리스트로 넣는것이 무슨의미인가! 관련(투표) 코드 제거할때 함께 제거

        return request;
    }
}
