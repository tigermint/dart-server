package com.ssh.dartserver.global.auth.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TokenResponse {
    private String jwtToken;
    private String tokenType;
    private LocalDateTime expiresAt;
    private String providerType;
    private String providerId;
}
