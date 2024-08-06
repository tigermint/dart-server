package com.ssh.dartserver.domain.auth.presentation.request;

import lombok.AllArgsConstructor;

import javax.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AppleTokenRequest {
    @NotBlank(message = "id token은 null 일 수 없습니다")
    private String idToken;
}
