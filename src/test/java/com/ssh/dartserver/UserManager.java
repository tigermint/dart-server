package com.ssh.dartserver;

import com.ssh.dartserver.global.auth.dto.AppleTokenRequest;
import com.ssh.dartserver.global.auth.dto.KakaoTokenRequest;
import com.ssh.dartserver.global.auth.dto.TokenResponse;
import com.ssh.dartserver.global.auth.service.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserManager {
    @Autowired
    private OAuthService mockOauthService;  // 통합테스트 환경에서 MockOAuthService가 대입됨

    /**
     * (Mock) 임의값을 기준으로 provider가 카카오인 TokenResponse를 생성합니다.
     * @param accessToken 카카오 accessToken과 대응합니다. 임의값을 넣어도 동작합니다.
     * @return TokenResponse 현재 서버에서 사용되는 JWT 포함
     */
    public TokenResponse kakaoLogin(String accessToken) {
        KakaoTokenRequest request = new KakaoTokenRequest();
        request.setAccessToken(accessToken);

        return mockOauthService.createTokenForKakao(request);
    }

    /**
     * (Mock) 임의값을 기준으로 provider가 애플인 TokenResponse를 생성합니다.
     * @param id 애플 id와 대응됩니다. 임의값을 넣어도 동작합니다.
     * @return TokenResponse 현재 서버에서 사용되는 JWT 포함
     */
    public TokenResponse appleLogin(String id) {
        AppleTokenRequest request = new AppleTokenRequest();
        request.setIdToken(id);

        return mockOauthService.createTokenForApple(request);
    }

    /**
     * (Mock) 테스트에 사용할 고정 유저의 TokenResponse를 반환합니다.
     * @return TokenResponse 현재 서버에서 사용되는 JWT 포함
     */
    public TokenResponse createTestUser() {
        KakaoTokenRequest request = new KakaoTokenRequest();
        request.setAccessToken("DEFAULT_TEST_TOKEN");

        return mockOauthService.createTokenForKakao(request);
    }

    /**
     * (Mock) count 변수만큼 반복하며 카카오 로그인 회원을 생성합니다.
     * @param count 생성할 카카오 로그인 회원 수
     */
    public void createUsers(int count) {
        for (int i=0; i<count; i++) {
            kakaoLogin("abcdefghijklmnop" + Math.random() + i);
        }
    }
}
