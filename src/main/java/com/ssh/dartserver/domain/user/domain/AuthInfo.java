package com.ssh.dartserver.domain.user.domain;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthInfo {

    @Column(unique = true)
    private String username;

    private String providerId;

    private String provider;

    private AuthInfo(
            final String username,
            final String providerId,
            final String provider
    ) {
        this.username = username;
        this.providerId = providerId;
        this.provider = provider;
    }

    public static AuthInfo of(
            final String username,
            final String providerId,
            final String provider
    ) {
        return new AuthInfo(username, providerId, provider);
    }
}
