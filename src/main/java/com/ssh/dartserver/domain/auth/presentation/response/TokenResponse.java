package com.ssh.dartserver.domain.auth.presentation.response;

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
    private String providerType;
    private String providerId;
}
