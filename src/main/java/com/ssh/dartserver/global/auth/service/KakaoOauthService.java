package com.ssh.dartserver.global.auth.service;

import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import com.ssh.dartserver.domain.user.domain.personalinfo.PersonalInfo;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.global.auth.domain.KakaoUser;
import com.ssh.dartserver.global.auth.domain.OAuthUserInfo;
import com.ssh.dartserver.global.auth.dto.TokenResponse;
import com.ssh.dartserver.global.auth.infra.KakaoOauthApi;
import com.ssh.dartserver.global.auth.service.jwt.JwtProperties;
import com.ssh.dartserver.global.auth.service.jwt.JwtToken;
import com.ssh.dartserver.global.auth.service.jwt.JwtTokenProvider;
import com.ssh.dartserver.global.common.Role;
import com.ssh.dartserver.global.error.KakaoLoginFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@OauthProviderType(OauthProvider.KAKAO)
public class KakaoOauthService implements OauthService {
    private final KakaoOauthApi kakaoOauthApi;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public TokenResponse createToken(final String providerToken) {
        return kakaoOauthApi.getKakaoUserInfo(providerToken).map(userInfo -> {
            OAuthUserInfo kakaoUser = new KakaoUser(userInfo);
            User userEntity = userRepository.findByUsername(kakaoUser.getProvider() + "_" + kakaoUser.getProviderId())
                .orElse(null);
            return getTokenResponseDto(kakaoUser, userEntity);
        }).orElseThrow(() -> new KakaoLoginFailedException("카카오 로그인에 실패하였습니다."));
    }

    private TokenResponse getTokenResponseDto(OAuthUserInfo oauthUser, User userEntity) {
        if (userEntity == null) {
            User userRequest = User.builder()
                .username(oauthUser.getProvider() + "_" + oauthUser.getProviderId())
                .password(bCryptPasswordEncoder.encode(JwtProperties.SECRET.getValue()))
                .provider(oauthUser.getProvider())
                .providerId(oauthUser.getProviderId())
                .role(Role.USER)
                .personalInfo(PersonalInfo.builder()
                    .gender(Gender.UNKNOWN)
                    .build())
                .build();
            userEntity = userRepository.save(userRequest);
        }

        //jwt 토큰 생성
        final JwtToken jwtToken = jwtTokenProvider.create(userEntity);
        return TokenResponse.builder()
            .jwtToken(jwtToken.getToken())
            .tokenType("BEARER")
            .expiresAt(jwtToken.getExpiresAt())
            .providerId(userEntity.getProviderId())
            .providerType(userEntity.getProvider())
            .build();
    }
}
