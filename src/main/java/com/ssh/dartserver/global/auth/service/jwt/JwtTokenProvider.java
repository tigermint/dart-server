package com.ssh.dartserver.global.auth.service.jwt;

import com.ssh.dartserver.global.auth.service.oauth.PrincipalDetails;
import com.ssh.dartserver.global.auth.service.oauth.PrincipalDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private final PrincipalDetailsService principalDetailsService;

    //인증 정보 조회
    public Authentication getAuthentication(JwtToken jwtToken) {
        PrincipalDetails principalDetails = principalDetailsService.loadUserByUsername(jwtToken.getUsername());
        return new UsernamePasswordAuthenticationToken(
                principalDetails,
                null,
                principalDetails.getAuthorities()
        );
    }
}
