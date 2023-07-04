package com.ssh.dartserver.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.ssh.dartserver.user.domain.User;
import com.ssh.dartserver.user.infra.persistence.UserRepository;
import com.ssh.dartserver.auth.domain.KakaoUser;
import com.ssh.dartserver.auth.domain.OAuthUserInfo;
import com.ssh.dartserver.auth.dto.request.TokenRequestDto;
import com.ssh.dartserver.auth.dto.response.TokenResponseDto;
import com.ssh.dartserver.auth.infra.KakaoRestTemplate;
import com.ssh.dartserver.auth.service.jwt.JwtProperties;
import com.ssh.dartserver.common.domain.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@RequiredArgsConstructor
@Service
public class OAuthService {
    private final KakaoRestTemplate kakaoRestTemplate;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public TokenResponseDto create(TokenRequestDto request) {

        return kakaoRestTemplate.getKakaoUserInfo(request.getAccessToken()).map(userInfo -> {
            OAuthUserInfo kakaoUser = new KakaoUser(userInfo);
            User userEntity = userRepository.findByUsername(kakaoUser.getProvider() + "_" + kakaoUser.getProviderId());

            if (userEntity == null) {
                User userRequest = User.builder()
                        .username(kakaoUser.getProvider() + "_" + kakaoUser.getProviderId())
                        .password(bCryptPasswordEncoder.encode(JwtProperties.SECRET.getValue()))
                        .provider(kakaoUser.getProvider())
                        .providerId(kakaoUser.getProviderId())
                        .role(Role.USER)
                        .build();
                userEntity = userRepository.save(userRequest);
            }
            String jwtToken = JWT.create()
                    .withSubject(userEntity.getUsername())
                    .withExpiresAt(new Date(System.currentTimeMillis() + Integer.parseInt(JwtProperties.EXPIRATION_TIME.getValue())))
                    .withClaim("id", userEntity.getId())
                    .withClaim("username", userEntity.getUsername())
                    .sign(Algorithm.HMAC512(JwtProperties.SECRET.getValue()));
            return new TokenResponseDto(jwtToken, userEntity.getProviderId());
        }).orElse(null);
    }
}



