package com.ssh.dartserver.global.auth.service.jwt;

import com.ssh.dartserver.global.error.CertificationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;


public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        super(authenticationManager);
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        //resolveToken - 토큰 추출
        String token = jwtTokenProvider.resolveToken(request);
        if(token == null) {
            chain.doFilter(request, response);
            return;
        }

        //validateToken - 토큰 유효성 검사
        if (jwtTokenProvider.validateToken(token)) {
            throw new CertificationException("유효하지 않은 토큰입니다.");
        }

        //getAuthentication - 인증 정보 조회
        Authentication authentication = jwtTokenProvider.getAuthentication(token);

        //SecurityContextHolder.getContext().setAuthentication - SecurityContext에 Authentication 객체 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
}
