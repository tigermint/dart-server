package com.ssh.dartserver.global.auth.service;

import com.ssh.dartserver.global.auth.dto.TokenResponse;

public interface OauthService {
    TokenResponse createToken(String providerToken);
}
