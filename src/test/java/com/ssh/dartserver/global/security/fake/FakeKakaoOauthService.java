package com.ssh.dartserver.global.security.fake;

import com.ssh.dartserver.domain.auth.presentation.response.TokenResponse;
import com.ssh.dartserver.domain.auth.domain.OauthProvider;
import com.ssh.dartserver.domain.auth.application.OauthProviderType;
import com.ssh.dartserver.domain.auth.application.OauthService;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
@OauthProviderType(OauthProvider.KAKAO)
public class FakeKakaoOauthService implements OauthService {
    @Override
    public TokenResponse createToken(final String providerToken) {
        return TokenResponse.builder()
            .jwtToken("123456789")
            .tokenType("JWT")
            .expiresAt(LocalDateTime.MAX)
            .providerType("KAKAO")
            .providerId("987654321")
            .build();
    }
}
