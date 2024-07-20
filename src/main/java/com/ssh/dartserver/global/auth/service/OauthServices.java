package com.ssh.dartserver.global.auth.service;

import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class OauthServices {
    private final Map<OauthProvider, OauthService> oauthServices;

    public OauthServices(final Map<OauthProvider, OauthService> oauthServices) {
        this.oauthServices = oauthServices;
    }

    public void put(OauthProvider provider, OauthService oauthService) {
        oauthServices.put(provider, oauthService);
    }

    public OauthService get(OauthProvider provider) {
        return oauthServices.get(provider);
    }

    public Set<OauthProvider> getSupportedProviders() {
        return oauthServices.keySet();
    }

    public static OauthServices createEmpty() {
        return new OauthServices(new EnumMap<>(OauthProvider.class));
    }
}
