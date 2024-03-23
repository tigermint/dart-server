package com.ssh.dartserver.global.auth.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class KakaoTokenRequest {
    @NotBlank(message = "access token은 null 일 수 없습니다")
    private String accessToken;
}
