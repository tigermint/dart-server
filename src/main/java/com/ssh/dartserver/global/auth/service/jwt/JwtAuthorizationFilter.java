package com.ssh.dartserver.global.auth.service.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.global.auth.service.oauth.PrincipalDetails;
import com.ssh.dartserver.global.error.CertificationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private final UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserRepository userRepository) {
        super(authenticationManager);
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader(JwtProperties.HEADER_STRING.getValue());

        if (header == null || !header.startsWith(JwtProperties.TOKEN_PREFIX.getValue())) {
            chain.doFilter(request, response);
            return;
        }

        String token = request.getHeader(JwtProperties.HEADER_STRING.getValue()).replace(JwtProperties.TOKEN_PREFIX.getValue(), "");
        String username = JWT.require(Algorithm.HMAC512(JwtProperties.SECRET.getValue())).build().verify(token)
                .getClaim("username").asString();

        if (username != null) {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new CertificationException("존재하지 않는 유저입니다."));
            PrincipalDetails principalDetails = new PrincipalDetails(user);
            Authentication authentication = new UsernamePasswordAuthenticationToken(principalDetails,
                    null,
                    principalDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        chain.doFilter(request, response);
    }
}
