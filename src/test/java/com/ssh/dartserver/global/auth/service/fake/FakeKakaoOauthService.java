package com.ssh.dartserver.global.auth.service.fake;

import com.ssh.dartserver.global.auth.dto.TokenResponse;
import com.ssh.dartserver.global.auth.service.OauthProvider;
import com.ssh.dartserver.global.auth.service.OauthProviderType;
import com.ssh.dartserver.global.auth.service.OauthService;
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
