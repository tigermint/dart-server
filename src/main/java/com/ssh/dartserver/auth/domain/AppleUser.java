package com.ssh.dartserver.auth.domain;

import java.util.Map;

public class AppleUser implements OAuthUserInfo{
    private final Map<String, Object> attribute;

    public AppleUser(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attribute.get("sub"));
    }

    @Override
    public String getProvider() {
        return "apple";
    }
}
