package com.ssh.dartserver.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssh.dartserver.auth.domain.AppleUser;
import com.ssh.dartserver.auth.domain.KakaoUser;
import com.ssh.dartserver.auth.domain.OAuthUserInfo;
import com.ssh.dartserver.auth.dto.AppleTokenRequest;
import com.ssh.dartserver.auth.dto.KakaoTokenRequest;
import com.ssh.dartserver.auth.dto.TokenResponse;
import com.ssh.dartserver.auth.infra.OAuthRestTemplate;
import com.ssh.dartserver.auth.service.jwt.JwtProperties;
import com.ssh.dartserver.common.domain.Role;
import com.ssh.dartserver.common.exception.AppleLoginFailedException;
import com.ssh.dartserver.common.exception.ApplePublicKeyNotFoundException;
import com.ssh.dartserver.user.domain.User;
import com.ssh.dartserver.user.domain.personalinfo.Gender;
import com.ssh.dartserver.user.domain.personalinfo.PersonalInfo;
import com.ssh.dartserver.auth.dto.ApplePublicKey;
import com.ssh.dartserver.auth.dto.GetApplePublicKeyResponse;
import com.ssh.dartserver.user.infra.persistence.UserRepository;
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
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OAuthService {
    private final OAuthRestTemplate oauthRestTemplate;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public TokenResponse createTokenForKakao(KakaoTokenRequest request) {
        return oauthRestTemplate.getKakaoUserInfo(request.getAccessToken()).map(userInfo -> {
            OAuthUserInfo kakaoUser = new KakaoUser(userInfo);
            User userEntity = userRepository.findByUsername(kakaoUser.getProvider() + "_" + kakaoUser.getProviderId());

            return getTokenResponseDto(kakaoUser, userEntity);
        }).orElse(null);
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

            ApplePublicKey possibleApplePublicKey = applePublicKeys.getKeys().stream()
                    .filter(key -> key.getKid().equals(header.get("kid")))
                    .findFirst()
                    .orElseThrow(() -> new ApplePublicKeyNotFoundException("일치하는 Apple Public Key가 없습니다."));

            RSAPublicKey publicKey = getPublicKey(possibleApplePublicKey.getN(), possibleApplePublicKey.getE())
                    .orElseThrow(() -> new ApplePublicKeyNotFoundException("일치하는 Apple Public Key가 없습니다."));

            Algorithm algorithm = Algorithm.RSA256(publicKey, null);
            JWT.require(algorithm).build().verify(idToken);

            OAuthUserInfo appleUser = new AppleUser(payload);
            User userEntity = userRepository.findByUsername("apple_" + payload.get("sub"));

            return getTokenResponseDto(appleUser, userEntity);
        } catch (ApplePublicKeyNotFoundException | AppleLoginFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new AppleLoginFailedException("Apple 로그인에 실패하였습니다.", e);
        }
    }

    private TokenResponse getTokenResponseDto(OAuthUserInfo appleUser, User userEntity) {
        if (userEntity == null) {
            User userRequest = User.builder()
                    .username(appleUser.getProvider() + "_" + appleUser.getProviderId())
                    .password(bCryptPasswordEncoder.encode(JwtProperties.SECRET.getValue()))
                    .provider(appleUser.getProvider())
                    .providerId(appleUser.getProviderId())
                    .role(Role.USER)
                    .personalInfo(PersonalInfo.builder()
                            .gender(Gender.UNKNOWN)
                            .build())
                    .build();
            userEntity = userRepository.save(userRequest);
        }

        String jwtToken = JWT.create()
                .withSubject(userEntity.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + Integer.parseInt(JwtProperties.EXPIRATION_TIME.getValue())))
                .withClaim("id", userEntity.getId())
                .withClaim("username", userEntity.getUsername())
                .sign(Algorithm.HMAC512(JwtProperties.SECRET.getValue()));
        return new TokenResponse(jwtToken, userEntity.getProviderId());
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



