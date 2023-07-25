package com.ssh.dartserver.global.auth.infra;

import com.ssh.dartserver.global.auth.dto.GetApplePublicKeyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class OAuthRestTemplate {
    private final RestTemplate restTemplate;

    /**
     * 카카오 유저 정보 가져오기
     * @param accessToken
     * @return
     */

    public Optional<Map<String, Object>> getKakaoUserInfo(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        Map<String, Object> userInfo = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<Map<String, Object>>() {
                }
        ).getBody();
        return Optional.ofNullable(userInfo);
    }

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
