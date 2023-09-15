package com.ssh.dartserver.global.auth.service.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.global.auth.service.oauth.PrincipalDetails;
import com.ssh.dartserver.global.auth.service.oauth.PrincipalDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final PrincipalDetailsService principalDetailsService;

    //JWT 토큰 생성
    public String createToken(User user) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + Integer.parseInt(JwtProperties.EXPIRATION_TIME.getValue())))
                .withClaim("id", user.getId())
                .withClaim("username", user.getUsername())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET.getValue()));
    }

    //인증 정보 조회
    public Authentication getAuthentication(String token) {
        PrincipalDetails principalDetails = (PrincipalDetails) principalDetailsService.loadUserByUsername(getUsername(token));
        return new UsernamePasswordAuthenticationToken(
                principalDetails,
                null,
                principalDetails.getAuthorities()
        );
    }

    //토큰에서 회원 정보 추출
    public String getUsername(String token) {
        return JWT.require(Algorithm.HMAC512(JwtProperties.SECRET.getValue()))
                .build()
                .verify(token)
                .getClaim("username")
                .asString();
    }

    //request header 에서 token 값을 가져옴 "Authorization: "TOKEN"
    public String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(JwtProperties.HEADER_STRING.getValue());
        if (header != null && header.startsWith(JwtProperties.TOKEN_PREFIX.getValue())) {
            return header.replace(JwtProperties.TOKEN_PREFIX.getValue(), "");
        }
        return null;
    }

    //토큰 유효성 검사 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            DecodedJWT jwt = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET.getValue()))
                    .build()
                    .verify(jwtToken);
            return validateTokenExpired(jwt);
        } catch (Exception e) {
            return true;
        }
    }

    private static boolean validateTokenExpired(DecodedJWT jwt) {
        Date expiresAt = jwt.getExpiresAt();
        return expiresAt.before(new Date());
    }
}
