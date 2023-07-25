package com.ssh.dartserver.global.auth.dto;

import lombok.Data;

@Data
public class TokenResponse {
    private String jwtToken;
    private String providerId;

    public TokenResponse(String jwtToken, String providerId) {
        this.jwtToken = jwtToken;
        this.providerId = providerId;
    }
}
