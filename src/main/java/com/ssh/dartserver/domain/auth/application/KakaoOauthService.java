package com.ssh.dartserver.domain.auth.application;

import com.ssh.dartserver.domain.auth.domain.OauthProvider;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.domain.auth.domain.KakaoUser;
import com.ssh.dartserver.domain.auth.domain.OAuthUserInfo;
import com.ssh.dartserver.domain.auth.presentation.response.TokenResponse;
import com.ssh.dartserver.domain.auth.infra.KakaoOauthApi;
import com.ssh.dartserver.global.security.jwt.JwtTokenProvider;
import com.ssh.dartserver.global.error.KakaoLoginFailedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@OauthProviderType(OauthProvider.KAKAO)
public class KakaoOauthService extends OauthServiceAbstract {
    private final KakaoOauthApi kakaoOauthApi;

    public KakaoOauthService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenProvider jwtTokenProvider, KakaoOauthApi kakaoOauthApi) {
        super(userRepository, bCryptPasswordEncoder, jwtTokenProvider);
        this.kakaoOauthApi = kakaoOauthApi;
    }

    @Override
    public TokenResponse createToken(final String providerToken) {
        return kakaoOauthApi.getKakaoUserInfo(providerToken)
            .map(userInfo -> {
                OAuthUserInfo kakaoUser = new KakaoUser(userInfo);
                User userEntity = userRepository.findByAuthInfo_Username(
                        kakaoUser.getProvider() + "_" + kakaoUser.getProviderId())
                    .orElse(null);
                return getTokenResponseDto(kakaoUser, userEntity);
            }).orElseThrow(() -> new KakaoLoginFailedException("카카오 로그인에 실패하였습니다."));
    }
}
