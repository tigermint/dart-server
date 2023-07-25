package com.ssh.dartserver.global.auth.domain;

import java.util.Map;

public class KakaoUser implements OAuthUserInfo {

    private final Map<String, Object> attribute;

    public KakaoUser(Map<String, Object> attribute) {
        this.attribute = attribute;
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attribute.get("id"));
    }

    @Override
    public String getProvider() {
        return "kakao";
    }
}
