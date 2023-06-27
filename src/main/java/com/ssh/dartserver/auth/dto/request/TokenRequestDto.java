package com.ssh.dartserver.auth.dto.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class TokenRequestDto {
    @NotBlank
    private String accessToken;
}
