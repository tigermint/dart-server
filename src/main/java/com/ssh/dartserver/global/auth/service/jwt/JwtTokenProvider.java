package com.ssh.dartserver.global.auth.service.jwt;

import static com.ssh.dartserver.global.auth.service.jwt.JwtToken.ID_CLAIM;
import static com.ssh.dartserver.global.auth.service.jwt.JwtToken.USERNAME_CLAIM;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.global.auth.service.oauth.PrincipalDetails;
import com.ssh.dartserver.global.auth.service.oauth.PrincipalDetailsService;
import com.ssh.dartserver.global.config.properties.JwtProperty;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final PrincipalDetailsService principalDetailsService;
    private final JwtProperty jwtProperty;

    /**
     * 전달된 JwtToken이 유효한지 검사합니다.
     * @param jwtToken JwtToken
     * @return 값이 유효한 경우 Authentication 객체를 반환합니다.
     */
    public Authentication getAuthentication(JwtToken jwtToken) {
        PrincipalDetails principalDetails = principalDetailsService.loadUserByUsername(jwtToken.getUsername());
        return new UsernamePasswordAuthenticationToken(
                principalDetails,
                null,
                principalDetails.getAuthorities()
        );
    }

    public Authentication getAuthentication(String token) {
        return getAuthentication(decode(token));
    }

    /**
     * 문자열 형태의 토큰값을 해석하여 JwtToken 객체로 반환합니다.
     * @param token 문자여 형태의 JWT토큰
     * @return JwtToken
     * @throws com.auth0.jwt.exceptions.SignatureVerificationException The Token's Signature resulted invalid when verified using the Algorithm
     */
    public JwtToken decode(String token) {
        final DecodedJWT verify = JWT.require(getAlgorithm())
            .build()
            .verify(token);

        return new JwtToken(verify);
    }

    /**
     * UserEntity 정보를 기반으로 JwtToken을 생성합니다.
     * @param user 사용자 정보 Entity
     * @return JwtTken
     */
    public JwtToken create(User user) {
        final String token = JWT.create()
            .withSubject(user.getUsername())
            .withExpiresAt(
                new Date(System.currentTimeMillis() + jwtProperty.getExpirationTime()))
            .withClaim(ID_CLAIM, user.getId())
            .withClaim(USERNAME_CLAIM, user.getUsername())
            .sign(getAlgorithm());

        return new JwtToken(JWT.decode(token));
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC512(jwtProperty.getSecret());
    }
}
