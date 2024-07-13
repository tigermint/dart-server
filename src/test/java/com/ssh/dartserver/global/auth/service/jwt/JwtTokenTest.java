package com.ssh.dartserver.global.auth.service.jwt;

import static org.junit.jupiter.api.Assertions.*;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.global.config.properties.JwtProperty;
import com.ssh.dartserver.global.error.CertificationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtTokenTest {
    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    public void setUp() {
        final JwtProperty properties = new JwtProperty("SECRET", 123456789L);
        jwtTokenProvider = new JwtTokenProvider(null, properties);
    }

    @Test
    @DisplayName("유저 객체를 기준으로 JWT토큰을 생성한다.")
    public void test_token_creation_with_valid_user_data() {
        User user = createTestUser();

        JwtToken jwtToken = jwtTokenProvider.create(user);

        assertNotNull(jwtToken);
        assertEquals("testuser", jwtToken.getUsername());
    }

    @Test
    @DisplayName("생성된 토큰을 다시 디코딩해도 두 객체는 같다.")
    public void test_decode_token() {
        User user = createTestUser();

        JwtToken token1 = jwtTokenProvider.create(user);
        JwtToken token2 = jwtTokenProvider.decode(token1.getToken());

        assertEquals(token1, token2);
    }

    @Test
    @DisplayName("유효하지 않은 토큰 생성시 JWTDecodeException 예외가 발생한다.")
    public void test_extracting_username_from_malformed_token() {
        String malformedToken = "malformed.token.string";

        assertThrows(JWTDecodeException.class, () -> jwtTokenProvider.decode(malformedToken));
    }

    @Test
    @DisplayName("토큰값이 동일한 두 객체를 비교할 경우 동일하다.")
    public void test_equals_identical_decodedJwt() {
        String tokenString = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0dXNlciIsImlkIjoxLCJleHAiOjE3MjA5NzAyMDIsInVzZXJuYW1lIjoidGVzdHVzZXIifQ.jz3fcW5PRWOVhnS_nLUB39wpKELVN-Lj2Md7JINbMQ2hO8qkI2vBTzp8X32Zt2YcXsl51p9rbZmas_ZRXkMuEw";
        final JwtToken token1 = jwtTokenProvider.decode(tokenString);
        final JwtToken token2 = jwtTokenProvider.decode(tokenString);

        boolean result = token1.equals(token2);

        assertTrue(result);
    }

    @Test
    @DisplayName("만료된 토큰을 검증하면 Certification 예외가 발생한다.")
    public void test_validate_token_expired() {
        final JwtProperty properties = new JwtProperty("SECRET", -3600L);
        jwtTokenProvider = new JwtTokenProvider(null, properties);

        final JwtToken jwtToken = jwtTokenProvider.create(createTestUser());

        assertThrows(CertificationException.class, () -> jwtToken.validateToken());
    }

    @Test
    @DisplayName("정상 토큰을 검증하면 예외없이 넘어간다.")
    public void test_validate_token_non_expired() {
        final JwtToken jwtToken = jwtTokenProvider.create(createTestUser());

        jwtToken.validateToken();
    }

    private static User createTestUser() {
        User user = User.builder()
            .username("testuser")
            .id(1L)
            .build();
        return user;
    }
}