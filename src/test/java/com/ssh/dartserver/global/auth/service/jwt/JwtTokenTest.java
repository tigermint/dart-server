package com.ssh.dartserver.global.auth.service.jwt;

import static org.junit.jupiter.api.Assertions.*;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.ssh.dartserver.domain.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtTokenTest {
    @Test
    @DisplayName("유저 객체를 기준으로 JWT토큰을 생성한다.")
    public void test_token_creation_with_valid_user_data() {
        User user = User.builder()
            .username("testuser")
            .id(1L)
            .build();

        JwtToken jwtToken = JwtToken.create(user);

        assertNotNull(jwtToken);
        assertEquals("testuser", jwtToken.getUsername());
    }

    @Test
    @DisplayName("유효하지 않은 토큰 생성시 JWTDecodeException 예외가 발생한다.")
    public void test_extracting_username_from_malformed_token() {
        String malformedToken = "malformed.token.string";

        assertThrows(JWTDecodeException.class, () -> {
            new JwtToken(malformedToken).getUsername();
        });
    }

    @Test
    @DisplayName("토큰값이 동일한 두 객체를 비교할 경우 동일하다고 판단합니다.")
    public void test_equals_identical_decodedJwt() {
        String tokenString = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJrYWthb18yODE3MDU0MDM1IiwiaWQiOjEsImV4cCI6MTcyMTY2NTE3MywidXNlcm5hbWUiOiJrYWthb18yODE3MDU0MDM1In0.eg_yD-BvpMV1w7t89SpVjO6InjprmRROP07FAvdDoXvWnra1L8m61aJK44LAHlG819kP1XcK6aaqbq0gqX9RHA";
        JwtToken token1 = new JwtToken(tokenString);
        JwtToken token2 = new JwtToken(tokenString);

        boolean result = token1.equals(token2);

        assertTrue(result);
    }
}