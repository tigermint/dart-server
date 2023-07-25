package com.ssh.dartserver.global.auth.presentation;

import com.ssh.dartserver.global.auth.dto.TokenResponse;
import com.ssh.dartserver.global.auth.dto.AppleTokenRequest;
import com.ssh.dartserver.global.auth.dto.KakaoTokenRequest;
import com.ssh.dartserver.global.auth.service.OAuthService;
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
    public ResponseEntity<TokenResponse> jwtCreateForKakao(@Valid @RequestBody KakaoTokenRequest request) {
        return ResponseEntity.ok(oAuthService.createTokenForKakao(request));
    }

    @PostMapping("/v1/auth/apple")
    public ResponseEntity<TokenResponse> jwtCreateForApple(@Valid @RequestBody AppleTokenRequest request) {
        return ResponseEntity.ok(oAuthService.createTokenForApple(request));
    }
}
