package com.ssh.dartserver;

import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import com.ssh.dartserver.domain.user.presentation.v1.request.UserSignUpRequest;
import com.ssh.dartserver.domain.user.application.UserService;
import com.ssh.dartserver.global.auth.MockOauthService;
import com.ssh.dartserver.global.auth.dto.AppleTokenRequest;
import com.ssh.dartserver.global.auth.dto.KakaoTokenRequest;
import com.ssh.dartserver.global.auth.dto.TokenResponse;
import com.ssh.dartserver.global.auth.service.jwt.JwtToken;
import com.ssh.dartserver.global.auth.service.jwt.JwtTokenProvider;
import com.ssh.dartserver.global.auth.service.oauth.PrincipalDetails;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
public class UserManager {
    @Autowired
    private MockOauthService mockOauthService;
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
        KakaoTokenRequest request = new KakaoTokenRequest(accessToken);

        return mockOauthService.createTokenForKakao(request);
    }

    /**
     * (Mock) 임의값을 기준으로 provider가 애플인 TokenResponse를 생성합니다.
     * @param id 애플 id와 대응됩니다. 임의값을 넣어도 동작합니다.
     * @return TokenResponse 현재 서버에서 사용되는 JWT 포함
     */
    public TokenResponse appleLogin(String id) {
        AppleTokenRequest request = new AppleTokenRequest(id);

        return mockOauthService.createTokenForApple(request);
    }

    /**
     * (Mock) 테스트에 사용할 고정 유저의 TokenResponse를 반환합니다.
     * @return TokenResponse 현재 서버에서 사용되는 JWT 포함
     */
    public TokenResponse createTestUser() {
        KakaoTokenRequest request = new KakaoTokenRequest("DEFAULT_TEST_TOKEN");

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
    public TokenResponse createUserWithInformation(UserSignUpRequest request) {
        final TokenResponse tokenResponse = kakaoLogin("abcdefghijklmnop" + UUID.randomUUID());
        final JwtToken jwtToken = jwtTokenProvider.decode(tokenResponse.getJwtToken());

        final Authentication authentication = jwtTokenProvider.getAuthentication(jwtToken);
        final User user = ((PrincipalDetails) authentication.getPrincipal()).getUser();

        userService.signUp(user, request.toPersonalInfo(), request.getUniversityId());
        return tokenResponse;
    }

    /**
     * (Mock) 임의정보로 회원가입을 완료한 카카오 유저를 생성합니다.
     * @return
     */
    public TokenResponse createTestUserWithInformation() {
        final UserSignUpRequest signupRequest = UserSignUpRequest.builder()
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
        final UserSignUpRequest signupRequest = UserSignUpRequest.builder()
                .universityId(1L)
                .admissionYear(2010)
                .birthYear(2005)
                .name("테스트")
                .phone("01012345678")
                .gender(gender)
                .build();

        return createUserWithInformation(signupRequest);
    }

    public User getUser(String jwtToken) {
        final Authentication authentication = jwtTokenProvider.getAuthentication(jwtToken);
        return ((PrincipalDetails) authentication.getPrincipal()).getUser();
    }
}
