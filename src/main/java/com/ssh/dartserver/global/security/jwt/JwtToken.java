package com.ssh.dartserver.global.security.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.ssh.dartserver.global.error.CertificationException;
import com.ssh.dartserver.global.util.DateTypeConverter;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

public class JwtToken {
    public static final String USERNAME_CLAIM = "username";
    public static final String ID_CLAIM = "id";
    private final DecodedJWT decodedJwt;

    public JwtToken(final DecodedJWT decodedJwt) {
        this.decodedJwt = decodedJwt;
    }

    public void validateToken() {
        Date now = new Date();
        if (now.before(decodedJwt.getExpiresAt())) {
            return;
        }
        throw new CertificationException("유효하지 않은 토큰입니다.");
    }

    public String getToken() {
        return decodedJwt.getToken();
    }

    public String getUsername() {
        return decodedJwt.getClaim(USERNAME_CLAIM)
            .asString();
    }

    public LocalDateTime getExpiresAt() {
        return DateTypeConverter.toLocalDateTime(
            decodedJwt.getExpiresAt()
        );
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final JwtToken jwtToken = (JwtToken) o;
        return Objects.equals(decodedJwt.getToken(), jwtToken.decodedJwt.getToken());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(decodedJwt.getToken());
    }
}
