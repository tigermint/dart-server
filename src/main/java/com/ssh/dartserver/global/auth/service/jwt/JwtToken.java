package com.ssh.dartserver.global.auth.service.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.global.util.DateTypeConverter;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

public class JwtToken {
    private static final String USERNAME_CLAIM = "username";
    private static final String ID_CLAIM = "id";
    private final DecodedJWT decodedJwt;

    public JwtToken(final String token) {
        decodedJwt = getDecodedJwt(token);
    }

    public boolean validateToken() {
        return validateTokenExpired(decodedJwt);
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

    private DecodedJWT getDecodedJwt(String token) {
        return JWT.require(Algorithm.HMAC512(JwtProperties.SECRET.getValue()))
            .build()
            .verify(token);
    }

    private boolean validateTokenExpired(DecodedJWT jwt) {
        Date expiresAt = jwt.getExpiresAt();
        return expiresAt.before(new Date());
    }

    public static JwtToken create(User user) {
        final String token = JWT.create()
            .withSubject(user.getUsername())
            .withExpiresAt(
                new Date(System.currentTimeMillis() + Integer.parseInt(JwtProperties.EXPIRATION_TIME.getValue())))
            .withClaim(ID_CLAIM, user.getId())
            .withClaim(USERNAME_CLAIM, user.getUsername())
            .sign(Algorithm.HMAC512(JwtProperties.SECRET.getValue()));

        return new JwtToken(token);
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
        return Objects.hashCode(decodedJwt);
    }
}
