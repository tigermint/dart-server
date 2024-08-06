package com.ssh.dartserver.domain.auth.application;

import com.ssh.dartserver.domain.auth.presentation.response.TokenResponse;
import com.ssh.dartserver.global.security.jwt.JwtToken;

public interface OauthService {
    JwtToken createToken(String providerToken);
}
