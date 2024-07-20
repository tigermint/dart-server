package com.ssh.dartserver.global.auth.service;

import com.ssh.dartserver.global.auth.dto.TokenResponse;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class OauthServiceFactory {
    private final Map<OauthProvider, OauthService> oauthServices;

    public OauthServiceFactory(List<OauthService> oauthServices) {
        this.oauthServices = new EnumMap<>(OauthProvider.class);
        for (OauthService service : oauthServices) {
            OauthProviderType type = service.getClass().getAnnotation(OauthProviderType.class);
            if (type == null) continue;
            this.oauthServices.put(type.value(), service);
            log.info("지원하는 OAuthProvider를 추가합니다. {} {}", type, service.getClass().getName());
        }
    }

    public TokenResponse getTokenResponse(OauthProvider provider, String providerToken) {
        // TODO OauthProvider를 지원하지 않는 경우에 대한 처리
        final OauthService oauthService = oauthServices.get(provider);
        return oauthService.createToken(providerToken);
    }

    public Set<OauthProvider> getSupportedOauth() {
        return oauthServices.keySet();
    }
}
