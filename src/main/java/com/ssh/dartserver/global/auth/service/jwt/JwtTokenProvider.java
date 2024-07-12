package com.ssh.dartserver.global.auth.service.jwt;

import com.ssh.dartserver.global.auth.service.oauth.PrincipalDetails;
import com.ssh.dartserver.global.auth.service.oauth.PrincipalDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider implements AuthenticationProvider {
    private final PrincipalDetailsService principalDetailsService;
    private final JwtTokenUtil jwtTokenUtil;

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        // TODO Provider 구현
        return null;
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        return false;
    }

    //인증 정보 조회
    public Authentication getAuthentication(String token) {
        PrincipalDetails principalDetails = principalDetailsService.loadUserByUsername(jwtTokenUtil.getUsername(token));
        return new UsernamePasswordAuthenticationToken(
                principalDetails,
                null,
                principalDetails.getAuthorities()
        );
    }
}
