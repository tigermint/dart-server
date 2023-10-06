package com.ssh.dartserver.global.config;

import com.ssh.dartserver.global.auth.service.jwt.JwtAuthenticationFilter;
import com.ssh.dartserver.global.auth.service.jwt.JwtAuthorizationFilter;
import com.ssh.dartserver.global.auth.service.jwt.JwtTokenProvider;
import com.ssh.dartserver.global.error.ExceptionHandlerFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private final CorsConfig corsConfig;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtTokenProvider jwtTokenProvider;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http
                .addFilter(corsConfig.corsFilter())
                .addFilter(new JwtAuthenticationFilter(authenticationConfiguration.getAuthenticationManager()))
                .addFilter(new JwtAuthorizationFilter(authenticationConfiguration.getAuthenticationManager(), jwtTokenProvider))
                .addFilterBefore(new ExceptionHandlerFilter(), JwtAuthenticationFilter.class);
        http
                .authorizeRequests()
                .antMatchers("/v1/auth/**",
                        "/v1/health/**",
                        "/swagger-ui/**",
                        "/swagger-resources/**",
                        "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated();
        return http.build();
    }
}
