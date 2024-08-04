package com.ssh.dartserver.global.auth;

import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.domain.auth.presentation.request.AppleTokenRequest;
import com.ssh.dartserver.domain.auth.presentation.request.KakaoTokenRequest;
import com.ssh.dartserver.domain.auth.presentation.response.TokenResponse;
import com.ssh.dartserver.domain.auth.application.OauthService;
import com.ssh.dartserver.global.security.jwt.JwtToken;
import com.ssh.dartserver.global.security.jwt.JwtTokenProvider;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * OAuth 로그인을 외부 API에 의존하지 않도록 Mocking (통합테스트 의존)
 */
@Component
@Primary
public class MockOauthService implements OauthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    public MockOauthService(final UserRepository userRepository, final JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public TokenResponse createToken(final String providerToken) {
        return createTokenForTest("dart", generateHash(providerToken));
    }

    public TokenResponse createTokenForKakao(KakaoTokenRequest request) {
        return createTokenForTest("kakao", generateHash(request.getAccessToken()));
    }

    public TokenResponse createTokenForApple(AppleTokenRequest request) {
        return createTokenForTest("apple", generateHash(request.getIdToken()));
    }

    private TokenResponse createTokenForTest(String provider, String id) {
        User userEntity = userRepository.findByAuthInfo_Username(provider + "_" + id)
            .orElse(null);
        return getTokenResponseDto(provider, id, userEntity);
    }

    private TokenResponse getTokenResponseDto(String provider, String id, User userEntity) {
        if (userEntity == null) {
            final User userRequest = User.of(provider + "_" + id, id, provider);
            userEntity = userRepository.save(userRequest);
        }

        //jwt 토큰 생성
        final JwtToken jwtToken = jwtTokenProvider.create(userEntity);
        return TokenResponse.builder()
            .jwtToken(jwtToken.getToken())
            .tokenType("BEARER")
            .expiresAt(jwtToken.getExpiresAt())
            .providerId(userEntity.getAuthInfo().getProviderId())
            .providerType(userEntity.getAuthInfo().getProvider())
            .build();
    }

    private static String generateHash(String inputString) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = md.digest(inputString.getBytes());

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < hashBytes.length; i++) {
                sb.append(String.format("%02d", hashBytes[i] & 0xFF));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            throw new IllegalStateException("랜덤 hash를 생성할 수 없습니다.");
        }
    }
}
