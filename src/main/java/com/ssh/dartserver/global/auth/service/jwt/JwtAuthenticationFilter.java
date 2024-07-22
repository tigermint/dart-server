package com.ssh.dartserver.global.auth.service.jwt;

import com.ssh.dartserver.global.config.properties.JwtProperty;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends BasicAuthenticationFilter {
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        super(authenticationManager);
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        // resolveToken - 토큰 추출
        String token = resolveToken(request);
        if(token == null) {
            chain.doFilter(request, response);
            return;
        }

        final JwtToken jwtToken = jwtTokenProvider.decode(token);

        // validateToken - 토큰 유효성 검사
        jwtToken.validateToken();

        // getAuthentication - 인증 정보 조회
        Authentication authentication = jwtTokenProvider.getAuthentication(jwtToken);

        // SecurityContext에 Authentication 객체 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    //request header 에서 token 값을 가져옴 "Authorization: "TOKEN"
     String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(JwtProperty.HEADER_STRING);
        if (header != null && header.startsWith(JwtProperty.TOKEN_PREFIX)) {
            return header.replace(JwtProperty.TOKEN_PREFIX, "");
        }
        return null;
    }
}
