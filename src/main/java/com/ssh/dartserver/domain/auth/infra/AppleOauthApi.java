package com.ssh.dartserver.domain.auth.infra;

import com.ssh.dartserver.domain.auth.presentation.request.ApplePublicKeyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class AppleOauthApi {
    private final RestTemplate restTemplate;

    /**
     * id token 검증을 위한 apple 공개키 가져오기
     * @return Apple에게 받은 ApplePublicKey들
     * @see ApplePublicKeyResponse
     */
    public ApplePublicKeyResponse getApplePublicKey() {
        return restTemplate.getForObject(
            "https://appleid.apple.com/auth/keys",
            ApplePublicKeyResponse.class
        );
    }
}
