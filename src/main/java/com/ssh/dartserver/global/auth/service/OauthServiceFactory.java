package com.ssh.dartserver.global.auth.service;

import com.ssh.dartserver.global.auth.dto.TokenResponse;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OauthServiceFactory {
    private final OauthServices oauthServices;

    /**
     * (생성자) Provider와 OauthService를 맵핑할 OauthServices 컬렉션을 초기화합니다.
     * @param oauthServices List로 감싸진 OAuthService 인터페이스의 구현체들
     */
    public OauthServiceFactory(List<OauthService> oauthServices) {
        this.oauthServices = OauthServices.createEmpty();
        oauthServices.stream()
            .filter(this::hasOauthProviderTypeAnnotation)
            .forEach(this::registerService);
    }

    /**
     * 전달된 OAuth 정보를 검증하여 JWT 토큰을 생성합니다.
     * @param provider OAuth 서비스 제공자 구분
     * @param providerToken 해당 서비스 제공자가 발행한 토큰 값
     * @return Dart 서버에서 사용할 JWT 토큰 정보
     * @throws IllegalArgumentException 지원하지 않는 Provider인 경우
     */
    public TokenResponse getTokenResponse(OauthProvider provider, String providerToken) {
        final OauthService oauthService = oauthServices.get(provider);
        if (oauthService == null) {
            throw new IllegalArgumentException("지원하지 않는 Provider입니다. Provider: " + provider);
        }
        return oauthService.createToken(providerToken);
    }

    /**
     * 지원하는 OAuth 서비스 제공자 목록을 반환합니다.
     * @return OAuth 서비스 제공자 목록
     */
    public Set<OauthProvider> getSupportedProviders() {
        return oauthServices.getSupportedProviders();
    }

    private boolean hasOauthProviderTypeAnnotation(OauthService service) {
        return service.getClass().isAnnotationPresent(OauthProviderType.class);
    }

    private void registerService(OauthService service) {
        OauthProviderType type = service.getClass().getAnnotation(OauthProviderType.class);
        this.oauthServices.put(type.value(), service);
        log.info("지원하는 OAuthProvider를 추가합니다. {} {}", type.value(), service.getClass().getName());
    }
}
