package com.ssh.dartserver.global.auth;

import com.ssh.dartserver.domain.auth.presentation.request.AppleTokenRequest;
import com.ssh.dartserver.domain.auth.presentation.request.KakaoTokenRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.springframework.http.MediaType;

public class AuthSteps {
    public static ExtractableResponse<Response> 카카오로그인요청(final KakaoTokenRequest request) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when()
            .post("/v1/auth/kakao")
            .then()
            .log().all().extract();
    }

    public static ExtractableResponse<Response> 애플로그인요청(final AppleTokenRequest request) {
        return RestAssured.given().log().all()
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .body(request)
            .when()
            .post("/v1/auth/apple")
            .then()
            .log().all().extract();
    }
}
