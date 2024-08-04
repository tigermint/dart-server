package com.ssh.dartserver.global.security;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ssh.dartserver.domain.auth.domain.OauthProvider;
import com.ssh.dartserver.domain.auth.application.OauthServiceFactory;
import com.ssh.dartserver.domain.auth.presentation.response.TokenResponse;
import com.ssh.dartserver.global.security.fake.FakeKakaoOauthService;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = {OauthServiceFactory.class, FakeKakaoOauthService.class})
class OauthServiceFactoryTest {
    @Autowired
    OauthServiceFactory oAuthServiceFactory;

    @Test
    @DisplayName("정상적으로 OAuthService를 스캔한다.")
    void scan() {
        final Set<OauthProvider> supportedProviders = oAuthServiceFactory.getSupportedProviders();

        assertThat(supportedProviders).contains(OauthProvider.KAKAO);
    }

    @Test
    @DisplayName("매칭되는 Provider가 있을시 요청을 수행하고 토큰을 반환한다.")
    void has_provider() {
        final TokenResponse token = oAuthServiceFactory.getTokenResponse(OauthProvider.KAKAO, "JUST_TEST_TOKEN");

        assertThat(token.getJwtToken()).isEqualTo("123456789");
    }

    @Test
    @DisplayName("매칭되는 Provider가 없을 경우 IllegalArgumentException이 발생한다.")
    void has_not_provider() {
        assertThrows(IllegalArgumentException.class, () ->
            oAuthServiceFactory.getTokenResponse(null, "JUST_TEST_TOKEN"));
    }
}
