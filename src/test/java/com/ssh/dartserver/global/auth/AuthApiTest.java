package com.ssh.dartserver.global.auth;

import static com.ssh.dartserver.global.auth.AuthSteps.애플로그인요청;
import static com.ssh.dartserver.global.auth.AuthSteps.카카오로그인요청;
import static org.assertj.core.api.Assertions.*;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.global.auth.dto.AppleTokenRequest;
import com.ssh.dartserver.global.auth.dto.KakaoTokenRequest;
import com.ssh.dartserver.global.auth.dto.TokenResponse;
import com.ssh.dartserver.testing.IntegrationTest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

@IntegrationTest
public class AuthApiTest extends ApiTest {
    @Test
    void 카카오로그인() {
        final KakaoTokenRequest request = new KakaoTokenRequest("defaultTestUser");

        final ExtractableResponse<Response> response = 카카오로그인요청(request);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }

    @Test
    void 카카오로그인_여러번해도_동일한사용자여야함() {
        final KakaoTokenRequest request = new KakaoTokenRequest("defaultTestUser");

        Set<String> userIds = new HashSet<>();
        for (int i=0; i<3; i++) {
            final ExtractableResponse<Response> response = 카카오로그인요청(request);

            final TokenResponse tokenResponse = response.body().as(TokenResponse.class);
            userIds.add(tokenResponse.getProviderId());
        }

        assertThat(userIds.size()).isEqualTo(1);
    }

    @Test
    void 애플로그인() {
        final AppleTokenRequest request = new AppleTokenRequest("defaultTestUser");

        final ExtractableResponse<Response> response = 애플로그인요청(request);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
    }
}
