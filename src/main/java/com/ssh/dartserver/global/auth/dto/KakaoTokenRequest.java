package com.ssh.dartserver.global.auth.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class KakaoTokenRequest {
    @NotBlank
    private String accessToken;
}
