package com.ssh.dartserver.global.auth.service;

import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import com.ssh.dartserver.domain.user.domain.personalinfo.PersonalInfo;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.global.auth.domain.OAuthUserInfo;
import com.ssh.dartserver.global.auth.dto.TokenResponse;
import com.ssh.dartserver.global.auth.service.jwt.JwtToken;
import com.ssh.dartserver.global.auth.service.jwt.JwtTokenProvider;
import com.ssh.dartserver.global.common.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@RequiredArgsConstructor
public abstract class OauthServiceAbstract implements OauthService {
    protected final UserRepository userRepository;
    protected final BCryptPasswordEncoder bCryptPasswordEncoder;
    protected final JwtTokenProvider jwtTokenProvider;

    /**
     * 유저 정보가 있으면 JWT 토큰을 바로 반환하고, 없으면 생성한뒤에 그걸 기반으로 JWT Token을 반환합니다.
     * @param oauthUser OAuth 유저 정보
     * @param userEntity 저장소에서 조회한 유저 정보
     * @return
     */
    // FIXME 두가지 책임을 동시에 지고 있습니다. + Save에는 트랜잭셔널 어노테이션이 필요할까요?
    protected TokenResponse getTokenResponseDto(OAuthUserInfo oauthUser, User userEntity) {
        final User user = readOrCreateUser(oauthUser);

//        if (userEntity == null) {
//            User userRequest = User.builder()
//                .username(oauthUser.getProvider() + "_" + oauthUser.getProviderId())
//                .password(bCryptPasswordEncoder.encode(jwtTokenProvider.getSecret()))
//                .provider(oauthUser.getProvider())
//                .providerId(oauthUser.getProviderId())
//                .role(Role.USER)
//                .personalInfo(PersonalInfo.builder()
//                    .gender(Gender.UNKNOWN)
//                    .build())
//                .build();
//            userEntity = userRepository.save(userRequest);
//        }

        // jwt 토큰 생성
        final JwtToken jwtToken = jwtTokenProvider.create(user);
        return TokenResponse.builder()
            .jwtToken(jwtToken.getToken())
            .tokenType("BEARER")
            .expiresAt(jwtToken.getExpiresAt())
            .providerId(user.getAuthInfo().getProviderId())
            .providerType(user.getAuthInfo().getProvider())
            .build();
    }

    private User readOrCreateUser(final OAuthUserInfo oauthUser) {
        String username = oauthUser.getProvider() + "_" + oauthUser.getProviderId();

        return userRepository.findByAuthInfo_Username(username)
            .orElseGet(() -> createUser(oauthUser));
    }

    private User createUser(final OAuthUserInfo oauthUser) {
        User user = User.of(
            oauthUser.getProvider() + "_" + oauthUser.getProviderId(),
            oauthUser.getProviderId(),
            oauthUser.getProvider()
        );
        return userRepository.save(user);
    }
}
