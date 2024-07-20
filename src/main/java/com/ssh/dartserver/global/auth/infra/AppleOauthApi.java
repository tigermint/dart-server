package com.ssh.dartserver.global.auth.infra;

import com.ssh.dartserver.global.auth.dto.GetApplePublicKeyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class AppleOauthApi {
    private final RestTemplate restTemplate;

    /**
     * id token 검증을 위한 apple 공개키 가져오기
     * @return
     */
    public GetApplePublicKeyResponse getApplePublicKey() {
        return restTemplate.getForObject(
            "https://appleid.apple.com/auth/keys",
            GetApplePublicKeyResponse.class
        );
    }
}
