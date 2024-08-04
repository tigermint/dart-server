package com.ssh.dartserver.global.auth;

import com.ssh.dartserver.domain.auth.presentation.response.TokenResponse;
import com.ssh.dartserver.domain.auth.domain.OauthProvider;
import com.ssh.dartserver.domain.auth.application.OauthService;
import com.ssh.dartserver.domain.auth.application.OauthServiceFactory;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class MockOauthserviceFactory extends OauthServiceFactory {
    @Autowired
    OauthService mockOauthService;

    // 해당 클래스 동작과는 상관없음 (상속으로 인해 사용됨)
    public MockOauthserviceFactory(final List<OauthService> oauthServices) {
        super(oauthServices);
    }

    @Override
    public TokenResponse getTokenResponse(OauthProvider provider, String providerToken) {
        return mockOauthService.createToken(providerToken);
    }
}
