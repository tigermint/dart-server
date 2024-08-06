package com.ssh.dartserver.global.security.fake;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ssh.dartserver.domain.auth.presentation.response.TokenResponse;
import com.ssh.dartserver.domain.auth.domain.OauthProvider;
import com.ssh.dartserver.domain.auth.application.OauthProviderType;
import com.ssh.dartserver.domain.auth.application.OauthService;
import com.ssh.dartserver.global.security.jwt.JwtToken;
import java.time.LocalDateTime;
import org.springframework.stereotype.Service;

@Service
@OauthProviderType(OauthProvider.KAKAO)
public class FakeKakaoOauthService implements OauthService {
    private static final String FAKE_SECRET_CODE = "SECRET";

    @Override
    public JwtToken createToken(final String providerToken) {
        final TokenResponse token = TokenResponse.builder()
            .jwtToken("eyJhbGciOiJIUzUxMiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.4gNVPDXymDlppyUCLJBa0UNcs85RSE9f6Jj9A7gReLHrxrtq8UJEMbpfWOmiufxqNMijCJz09M_jbI4abYdL4w")
            .tokenType("JWT")
            .expiresAt(LocalDateTime.MAX)
            .build();

        System.out.println(token);
        System.out.println(token.getJwtToken());

        return decode(token.getJwtToken());
    }

    public JwtToken decode(String token) {
        final DecodedJWT verify = JWT.require(getAlgorithm())
            .build()
            .verify(token);

        return new JwtToken(verify);
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC512(FAKE_SECRET_CODE);
    }
}
