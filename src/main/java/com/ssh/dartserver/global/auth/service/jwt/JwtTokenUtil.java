package com.ssh.dartserver.global.auth.service.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.global.util.DateTypeConverter;
import java.time.LocalDateTime;
import java.util.Date;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenUtil {
    ///// JWT RECORD
    //토큰에서 회원 정보 추출
    public String getUsername(String token) {
        return getDecodedJwt(token)
            .getClaim("username")
            .asString();
    }

    //토큰에서 만료 시간 추출
    public LocalDateTime getExpiresAt(String token) {
        return DateTypeConverter.toLocalDateTime(
            getDecodedJwt(token).getExpiresAt()
        );
    }

    //토큰 유효성 검사 + 만료일자 확인
    public boolean validateToken(String token) {
        try {
            DecodedJWT jwt = getDecodedJwt(token);
            return validateTokenExpired(jwt);
        } catch (Exception e) {
            return true;
        }
    }


    /// TOKEN DECODER (UTIL)
    //JWT 토큰 생성
    public String createToken(User user) {
        return JWT.create()
            .withSubject(user.getUsername())
            .withExpiresAt(new Date(System.currentTimeMillis() + Integer.parseInt(JwtProperties.EXPIRATION_TIME.getValue())))
            .withClaim("id", user.getId())
            .withClaim("username", user.getUsername())
            .sign(Algorithm.HMAC512(JwtProperties.SECRET.getValue()));
    }

    private static DecodedJWT getDecodedJwt(String token) {
        return JWT.require(Algorithm.HMAC512(JwtProperties.SECRET.getValue()))
            .build()
            .verify(token);
    }

    private static boolean validateTokenExpired(DecodedJWT jwt) {
        Date expiresAt = jwt.getExpiresAt();
        return expiresAt.before(new Date());
    }
}
