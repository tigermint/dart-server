package com.ssh.dartserver.global.auth.service.jwt;

import static org.junit.jupiter.api.Assertions.*;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.global.config.properties.JwtProperty;
import com.ssh.dartserver.global.error.CertificationException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
    void test_token_creation_with_valid_user_data() {
        User user = createTestUser();

        JwtToken jwtToken = jwtTokenProvider.create(user);

        assertNotNull(jwtToken);
        assertEquals("testuser", jwtToken.getUsername());
        assertTrue(jwtToken.getExpiresAt().isAfter(LocalDateTime.now()));
    }

    @Test
    @DisplayName("생성된 토큰을 다시 디코딩해도 두 객체는 같다.")
    void test_decode_token() {
        User user = createTestUser();

        JwtToken token1 = jwtTokenProvider.create(user);
        JwtToken token2 = jwtTokenProvider.decode(token1.getToken());

        assertEquals(token1, token2);
    }

    @Test
    @DisplayName("유효하지 않은 토큰 생성시 JWTDecodeException 예외가 발생한다.")
    void test_extracting_username_from_malformed_token() {
        String malformedToken = "malformed.token.string";

        assertThrows(JWTDecodeException.class, () -> jwtTokenProvider.decode(malformedToken));
    }

    @Test
    @DisplayName("동일한 두 객체를 비교할 경우 동일하고 동등하다.")
    void test_equals_jwt() {
        User user = createTestUser();
        final JwtToken jwtToken = jwtTokenProvider.create(user);
        final String tokenString = jwtToken.getToken();

        final JwtToken token = jwtTokenProvider.decode(tokenString);

        assertTrue(token == token);
        assertEquals(token, token);
    }

    @Test
    @DisplayName("Null 또는 다른 클래스와는 항상 동등하지 않다.")
    void test_equals_null_and_another_class() {
        User user = createTestUser();
        final JwtToken jwtToken = jwtTokenProvider.create(user);
        final String tokenString = jwtToken.getToken();

        final JwtToken token = jwtTokenProvider.decode(tokenString);

        assertNotEquals(token, null);
        assertNotEquals(token, new JwtTokenTest());
    }

    @Test
    @DisplayName("토큰값이 동일한 두 객체를 비교할 경우 동등하다.")
    void test_equals_identical_decodedJwt() {
        User user = createTestUser();
        final JwtToken jwtToken = jwtTokenProvider.create(user);
        final String tokenString = jwtToken.getToken();

        final JwtToken token1 = jwtTokenProvider.decode(tokenString);
        final JwtToken token2 = jwtTokenProvider.decode(tokenString);

        assertFalse(token1 == token2);  // token1과 2는 다른 객체다.
        assertEquals(token1, token2);  // 하지만 동등하다.
    }

    @Test
    @DisplayName("토큰값이 다른 두 객체를 비교할 경우 동등하지 않다.")
    void equalsFailTest() {
        Algorithm algorithm = Algorithm.HMAC256("secret");
        String token1 = createTokenRaw(algorithm, "user1");
        String token2 = createTokenRaw(algorithm, "user2");

        JwtToken jwtToken1 = new JwtToken(JWT.decode(token1));
        JwtToken jwtToken2 = new JwtToken(JWT.decode(token2));

        assertNotEquals(jwtToken1, jwtToken2);
        assertFalse(jwtToken1 == jwtToken2);
    }

    @Test
    @DisplayName("토큰값이 같은 객체는 HashCode가 같다.")
    void hashCodeTest() {
        Algorithm algorithm = Algorithm.HMAC256("secret");
        String token1 = createTokenRaw(algorithm, "user1");
        String token2 = createTokenRaw(algorithm, "user1");

        JwtToken jwtToken1 = new JwtToken(JWT.decode(token1));
        JwtToken jwtToken2 = new JwtToken(JWT.decode(token2));

        // hashCode가 동일한지 확인
        assertEquals(jwtToken1.hashCode(), jwtToken2.hashCode());

        // HashSet을 사용하여 중복 제거 테스트
        Set<JwtToken> tokenSet = new HashSet<>();
        tokenSet.add(jwtToken1);
        tokenSet.add(jwtToken2);

        // 중복이 제거되어 크기가 1이 되어야 함
        assertEquals(1, tokenSet.size());
    }
    
    @Test
    @DisplayName("만료된 토큰을 검증하면 Certification 예외가 발생한다.")
    void test_validate_token_expired() {
        final JwtProperty properties = new JwtProperty("SECRET", -3600L);
        jwtTokenProvider = new JwtTokenProvider(null, properties);

        final JwtToken jwtToken = jwtTokenProvider.create(createTestUser());

        assertThrows(CertificationException.class, () -> jwtToken.validateToken());
    }

    @Test
    @DisplayName("정상 토큰을 검증하면 예외없이 넘어간다.")
    void test_validate_token_non_expired() {
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

    private static String createTokenRaw(final Algorithm algorithm, final String tokenValue) {
        return JWT.create()
            .withClaim(JwtToken.USERNAME_CLAIM, tokenValue)
            .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
            .sign(algorithm);
    }
}
