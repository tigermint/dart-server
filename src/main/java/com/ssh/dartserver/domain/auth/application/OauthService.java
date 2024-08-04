package com.ssh.dartserver.domain.auth.application;

import com.ssh.dartserver.domain.auth.presentation.response.TokenResponse;

public interface OauthService {
    TokenResponse createToken(String providerToken);
}
