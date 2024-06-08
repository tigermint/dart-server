package com.ssh.dartserver;

import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import com.ssh.dartserver.domain.user.dto.UserSignupRequest;
import com.ssh.dartserver.domain.user.service.UserService;
import com.ssh.dartserver.global.auth.dto.AppleTokenRequest;
import com.ssh.dartserver.global.auth.dto.KakaoTokenRequest;
import com.ssh.dartserver.global.auth.dto.TokenResponse;
import com.ssh.dartserver.global.auth.service.OAuthService;
import com.ssh.dartserver.global.auth.service.jwt.JwtTokenProvider;
import com.ssh.dartserver.global.auth.service.oauth.PrincipalDetails;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class UserManager {
    @Autowired
    private OAuthService mockOauthService;  // 통합테스트 환경에서 MockOAuthService가 대입됨
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private UserService userService;

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

    /**
     * (Mock) request를 토대로 회원가입을한 카카오 유저를 생성합니다.
     * @param request 회원가입시 입력하는 정보 DTO
     * @return
     */
    public TokenResponse createUserWithInformation(UserSignupRequest request) {
        final TokenResponse tokenResponse = kakaoLogin("abcdefghijklmnop" + UUID.randomUUID());
        final String jwtToken = tokenResponse.getJwtToken();

        final Authentication authentication = jwtTokenProvider.getAuthentication(jwtToken);
        final User user = ((PrincipalDetails) authentication.getPrincipal()).getUser();

        userService.signup(user, request);
        return tokenResponse;
    }

    /**
     * (Mock) 임의정보로 회원가입을 완료한 카카오 유저를 생성합니다.
     * @return
     */
    public TokenResponse createTestUserWithInformation() {
        final UserSignupRequest signupRequest = UserSignupRequest.builder()
            .universityId(1L)
            .admissionYear(2010)
            .birthYear(2005)
            .name("테스트")
            .phone("01012345678")
            .gender(Gender.MALE)
            .build();

        return createUserWithInformation(signupRequest);
    }

    /**
     * (Mock) 임의정보와 입력받은 성별로 회원가입을 완료한 카카오 유저를 생성합니다.
     * @param gender
     * @return
     */

    public TokenResponse createTestUserWithInformation(Gender gender) {
        final UserSignupRequest signupRequest = UserSignupRequest.builder()
                .universityId(1L)
                .admissionYear(2010)
                .birthYear(2005)
                .name("테스트")
                .phone("01012345678")
                .gender(gender)
                .build();

        return createUserWithInformation(signupRequest);
    }
}
