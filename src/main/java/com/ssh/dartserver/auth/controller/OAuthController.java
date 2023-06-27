package com.ssh.dartserver.auth.controller;

import com.ssh.dartserver.auth.dto.request.TokenRequestDto;
import com.ssh.dartserver.auth.dto.response.TokenResponseDto;
import com.ssh.dartserver.auth.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class OAuthController {
    private final OAuthService oAuthService;

    @PostMapping("/v1/auth/kakao")
    public ResponseEntity<TokenResponseDto> jwtCreate(@Valid @RequestBody TokenRequestDto request) {
        return ResponseEntity.ok(oAuthService.create(request));
    }
}
