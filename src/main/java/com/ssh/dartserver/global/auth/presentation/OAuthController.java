package com.ssh.dartserver.global.auth.presentation;

import com.ssh.dartserver.global.auth.dto.TokenResponse;
import com.ssh.dartserver.global.auth.dto.AppleTokenRequest;
import com.ssh.dartserver.global.auth.dto.KakaoTokenRequest;
import com.ssh.dartserver.global.auth.service.OauthServiceFactory;
import com.ssh.dartserver.global.auth.service.OauthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RequiredArgsConstructor
@RestController
public class OAuthController {
    private final OauthServiceFactory oAuthServiceFactory;

    @PostMapping("/v1/auth/kakao")
    public ResponseEntity<TokenResponse> jwtCreateForKakao(@Valid @RequestBody KakaoTokenRequest request) {
        final TokenResponse token = oAuthServiceFactory.getTokenResponse(OauthProvider.KAKAO, request.getAccessToken());
        return ResponseEntity.ok(token);
    }

    @PostMapping("/v1/auth/apple")
    public ResponseEntity<TokenResponse> jwtCreateForApple(@Valid @RequestBody AppleTokenRequest request) {
        final TokenResponse token = oAuthServiceFactory.getTokenResponse(OauthProvider.APPLE, request.getIdToken());
        return ResponseEntity.ok(token);
    }
}
