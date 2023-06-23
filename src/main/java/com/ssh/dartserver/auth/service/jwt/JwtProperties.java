package com.ssh.dartserver.auth.service.jwt;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum JwtProperties {
    SECRET("dart"),
    EXPIRATION_TIME("864000000"),
    TOKEN_PREFIX("Bearer "),
    HEADER_STRING("Authorization");

    private final String value;
}
