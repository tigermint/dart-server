package com.ssh.dartserver.global.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.global.auth.dto.TokenResponse;
import com.ssh.dartserver.global.auth.infra.KakaoOauthApi;
import com.ssh.dartserver.global.auth.service.jwt.JwtToken;
import com.ssh.dartserver.global.auth.service.jwt.JwtTokenProvider;
import com.ssh.dartserver.global.error.KakaoLoginFailedException;
import java.util.Date;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KakaoOauthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private KakaoOauthApi kakaoOauthApi;
    @InjectMocks
    private KakaoOauthService kakaoOauthService;

    @Test
    @DisplayName("카카오 토큰을 검증하고 정상적으로 JWT를 생성한다.")
    void createToken_WithNewUser_ShouldCreateNewUserAndReturnToken() {
        String providerToken = "kakao_token";
        String providerId = "kakao_1234";
        final Date expiresAt = new Date(System.currentTimeMillis() + 1000 * 60 * 10);
        String jwtToken = createTokenRaw("user2", expiresAt);
        JwtToken jwtToken1 = new JwtToken(JWT.decode(jwtToken));

        when(kakaoOauthApi.getKakaoUserInfo(providerToken)).thenReturn(Optional.of(Map.of("id", "kakao_1234")));
        when(userRepository.findByAuthInfo_Username("kakao_" + providerId)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(jwtTokenProvider.create(any(User.class))).thenReturn(jwtToken1);

        TokenResponse tokenResponse = kakaoOauthService.createToken(providerToken);

        assertNotNull(tokenResponse);
        assertEquals(jwtToken, tokenResponse.getJwtToken());
        assertEquals(providerId, tokenResponse.getProviderId());
        assertEquals("BEARER", tokenResponse.getTokenType().toUpperCase());
        assertEquals("kakao", tokenResponse.getProviderType().toLowerCase());
    }

    @Test
    @DisplayName("토큰이 유효하지 않은 경우 KakaoLoginFailedException이 발생합니다.")
    void createToken_WhenKakaoApiFailsApiCall_ShouldThrowException() {
        String providerToken = "kakao_token";
        when(kakaoOauthApi.getKakaoUserInfo(providerToken)).thenReturn(Optional.empty());

        assertThrows(KakaoLoginFailedException.class, () -> kakaoOauthService.createToken(providerToken));
    }

    private static String createTokenRaw(final String tokenValue, Date expiresAt) {
        Algorithm algorithm = Algorithm.HMAC256("secret");
        return JWT.create()
            .withClaim(JwtToken.USERNAME_CLAIM, tokenValue)
            .withExpiresAt(expiresAt)
            .sign(algorithm);
    }
}
