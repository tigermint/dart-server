package com.ssh.dartserver.global.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import com.ssh.dartserver.domain.user.domain.personalinfo.PersonalInfo;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.global.auth.domain.AppleUser;
import com.ssh.dartserver.global.auth.domain.KakaoUser;
import com.ssh.dartserver.global.auth.domain.OAuthUserInfo;
import com.ssh.dartserver.global.auth.dto.*;
import com.ssh.dartserver.global.auth.infra.OAuthRestTemplate;
import com.ssh.dartserver.global.auth.service.jwt.JwtProperties;
import com.ssh.dartserver.global.auth.service.jwt.JwtToken;
import com.ssh.dartserver.global.auth.service.jwt.JwtTokenProvider;
import com.ssh.dartserver.global.common.Role;
import com.ssh.dartserver.global.error.AppleLoginFailedException;
import com.ssh.dartserver.global.error.ApplePublicKeyNotFoundException;
import com.ssh.dartserver.global.error.KakaoLoginFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OAuthService {
    private final OAuthRestTemplate oauthRestTemplate;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public TokenResponse createTokenForKakao(KakaoTokenRequest request) {
        return oauthRestTemplate.getKakaoUserInfo(request.getAccessToken()).map(userInfo -> {
            OAuthUserInfo kakaoUser = new KakaoUser(userInfo);
            User userEntity = userRepository.findByUsername(kakaoUser.getProvider() + "_" + kakaoUser.getProviderId())
                    .orElse(null);
            return getTokenResponseDto(kakaoUser, userEntity);
        }).orElseThrow(() -> new KakaoLoginFailedException("카카오 로그인에 실패하였습니다."));
    }

    public TokenResponse createTokenForApple(AppleTokenRequest request) {
        String idToken = request.getIdToken();
        Base64.Decoder decoder = Base64.getDecoder();
        DecodedJWT jwt = JWT.decode(idToken);
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            GetApplePublicKeyResponse applePublicKeys = oauthRestTemplate.getApplePublicKey();

            String headerString = new String(decoder.decode(jwt.getHeader()), StandardCharsets.UTF_8);
            String payloadString = new String(decoder.decode(jwt.getPayload()), StandardCharsets.UTF_8);

            Map<String, Object> header = objectMapper.readValue(headerString, Map.class);
            Map<String, Object> payload = objectMapper.readValue(payloadString, Map.class);

            //apple 공개키 가져오기
            ApplePublicKey possibleApplePublicKey = applePublicKeys.getKeys().stream()
                    .filter(key -> key.getKid().equals(header.get("kid")))
                    .findFirst()
                    .orElseThrow(() -> new ApplePublicKeyNotFoundException("일치하는 Apple Public Key가 없습니다."));

            RSAPublicKey publicKey = getPublicKey(possibleApplePublicKey.getN(), possibleApplePublicKey.getE())
                    .orElseThrow(() -> new ApplePublicKeyNotFoundException("일치하는 Apple Public Key가 없습니다."));

            //apple id token 검증
            Algorithm algorithm = Algorithm.RSA256(publicKey, null);
            String possiblePayload = new String(decoder.decode(JWT.require(algorithm).build().verify(idToken).getPayload()), StandardCharsets.UTF_8);
            Map<String, Object> possiblePayloadMap = objectMapper.readValue(possiblePayload, Map.class);

            OAuthUserInfo appleUser = new AppleUser(possiblePayloadMap);

            //apple username 검색을 통한 기존 유저 확인
            User userEntity = userRepository.findByUsername("apple_" + payload.get("sub"))
                    .orElse(null);

            return getTokenResponseDto(appleUser, userEntity);
        } catch (ApplePublicKeyNotFoundException | AppleLoginFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new AppleLoginFailedException("유효하지 않은 Apple 토큰입니다", e);
        }
    }

    private TokenResponse getTokenResponseDto(OAuthUserInfo oauthUser, User userEntity) {
        if (userEntity == null) {
            User userRequest = User.builder()
                    .username(oauthUser.getProvider() + "_" + oauthUser.getProviderId())
                    .password(bCryptPasswordEncoder.encode(JwtProperties.SECRET.getValue()))
                    .provider(oauthUser.getProvider())
                    .providerId(oauthUser.getProviderId())
                    .role(Role.USER)
                    .personalInfo(PersonalInfo.builder()
                            .gender(Gender.UNKNOWN)
                            .build())
                    .build();
            userEntity = userRepository.save(userRequest);
        }

        //jwt 토큰 생성
        final JwtToken jwtToken = jwtTokenProvider.create(userEntity);
        return TokenResponse.builder()
                .jwtToken(jwtToken.getToken())
                .tokenType("BEARER")
                .expiresAt(jwtToken.getExpiresAt())
                .providerId(userEntity.getProviderId())
                .providerType(userEntity.getProvider())
                .build();
    }

    private static Optional<RSAPublicKey> getPublicKey(String modulusBase64, String exponentBase64) {
        try {
            BigInteger modulus = new BigInteger(1, Base64.getUrlDecoder().decode(modulusBase64));
            BigInteger exponent = new BigInteger(1, Base64.getUrlDecoder().decode(exponentBase64));

            RSAPublicKeySpec spec = new RSAPublicKeySpec(modulus, exponent);
            KeyFactory factory = KeyFactory.getInstance("RSA");

            return Optional.of((RSAPublicKey) factory.generatePublic(spec));
        } catch (IllegalArgumentException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            return Optional.empty();
        }
    }
}
