package com.ssh.dartserver.global.auth.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class AppleTokenRequest {
    @NotBlank(message = "id token은 null 일 수 없습니다")
    private String idToken;
}
