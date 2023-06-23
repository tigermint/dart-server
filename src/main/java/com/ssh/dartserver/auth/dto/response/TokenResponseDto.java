package com.ssh.dartserver.auth.dto.response;

import lombok.Data;

@Data
public class TokenResponseDto {
    private String jwtToken;
    private String providerId;

    public TokenResponseDto(String jwtToken, String providerId) {
        this.jwtToken = jwtToken;
        this.providerId = providerId;
    }
}
