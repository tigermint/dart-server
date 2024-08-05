package com.ssh.dartserver.domain.auth.presentation.response;

import com.ssh.dartserver.global.security.jwt.JwtToken;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenResponse {
    private String jwtToken;
    private String tokenType;
    private LocalDateTime expiresAt;
    @Deprecated(since = "20240806", forRemoval = true)  // 클라이언트 코드에서 제거될 때 제거
    private final String providerType = "DEPRECATED";
    @Deprecated(since = "20240806", forRemoval = true)  // 클라이언트 코드에서 제거될 때 제거
    private final String providerId = "DEPRECATED";

    public static TokenResponse from(JwtToken jwtToken) {
        return new TokenResponse(jwtToken.getToken(), "BEARER", jwtToken.getExpiresAt());
    }
}
