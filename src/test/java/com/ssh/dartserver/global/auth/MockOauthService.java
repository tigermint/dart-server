package com.ssh.dartserver.global.auth;

import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import com.ssh.dartserver.domain.user.domain.personalinfo.PersonalInfo;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.global.auth.dto.AppleTokenRequest;
import com.ssh.dartserver.global.auth.dto.KakaoTokenRequest;
import com.ssh.dartserver.global.auth.dto.TokenResponse;
import com.ssh.dartserver.global.auth.infra.OAuthRestTemplate;
import com.ssh.dartserver.global.auth.service.OAuthService;
import com.ssh.dartserver.global.auth.service.jwt.JwtTokenProvider;
import com.ssh.dartserver.global.auth.service.jwt.JwtTokenUtil;
import com.ssh.dartserver.global.common.Role;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * OAuth 로그인을 외부 API에 의존하지 않도록 Mocking (통합테스트 의존)
 */
@Component
@Primary
public class MockOauthService extends OAuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtTokenUtil jwtTokenUtil;

    public MockOauthService(final OAuthRestTemplate oauthRestTemplate,
                            final UserRepository userRepository,
                            final BCryptPasswordEncoder bCryptPasswordEncoder,
                            final JwtTokenProvider jwtTokenProvider,
                            final JwtTokenUtil jwtTokenUtil) {
        super(oauthRestTemplate, userRepository, bCryptPasswordEncoder, jwtTokenProvider, jwtTokenUtil);
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtTokenUtil = jwtTokenUtil;
    }


    public TokenResponse createTokenForKakao(KakaoTokenRequest request) {
        return createTokenForTest("kakao", generateHash(request.getAccessToken()));
    }

    public TokenResponse createTokenForApple(AppleTokenRequest request) {
        return createTokenForTest("apple", generateHash(request.getIdToken()));
    }

    private TokenResponse createTokenForTest(String provider, String id) {
        User userEntity = userRepository.findByUsername(provider + "_" + id)
            .orElse(null);
        return getTokenResponseDto(provider, id, userEntity);
    }

    private TokenResponse getTokenResponseDto(String provider, String id, User userEntity) {
        if (userEntity == null) {
            User userRequest = User.builder()
                .username(provider + "_" + id)
                .password("1234567890")
                .provider(provider)
                .providerId(id)
                .role(Role.USER)
                .personalInfo(PersonalInfo.builder()
                    .gender(Gender.UNKNOWN)
                    .build())
                .build();
            userEntity = userRepository.save(userRequest);
        }

        //jwt 토큰 생성
        String jwtToken = jwtTokenUtil.createToken(userEntity);
        return TokenResponse.builder()
            .jwtToken(jwtToken)
            .tokenType("BEARER")
            .expiresAt(jwtTokenUtil.getExpiresAt(jwtToken))
            .providerId(userEntity.getProviderId())
            .providerType(userEntity.getProvider())
            .build();
    }

    public static String generateHash(String inputString) {
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
