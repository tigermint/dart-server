package com.ssh.dartserver.domain.auth.application;

import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.domain.auth.domain.OAuthUserInfo;
import com.ssh.dartserver.domain.auth.presentation.response.TokenResponse;
import com.ssh.dartserver.global.security.jwt.JwtToken;
import com.ssh.dartserver.global.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class OauthServiceAbstract implements OauthService {
    protected final UserRepository userRepository;
    protected final JwtTokenProvider jwtTokenProvider;

    /**
     * 유저 정보가 있으면 JWT 토큰을 바로 반환하고, 없으면 생성한뒤에 그걸 기반으로 JWT Token을 반환합니다.
     * @param oauthUser OAuth 유저 정보
     * @return
     */
    protected JwtToken getTokenResponseDto(OAuthUserInfo oauthUser) {
        final User user = readOrCreateUser(oauthUser);
        return jwtTokenProvider.create(user);
    }

    /**
     * 등록된 User 정보를 찾아 반환합니다. 만약 없다면 새로 생성합니다.
     * @param oauthUser OAUTH 유저 정보, 해당 값을 통해 유저를 검색합니다.
     * @return 등록되어있던 User 객체 또는 새로 생성한 User 객체
     */
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
