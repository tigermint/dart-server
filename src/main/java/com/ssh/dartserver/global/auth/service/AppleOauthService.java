package com.ssh.dartserver.global.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.global.auth.domain.AppleUser;
import com.ssh.dartserver.global.auth.domain.OAuthUserInfo;
import com.ssh.dartserver.global.auth.dto.ApplePublicKey;
import com.ssh.dartserver.global.auth.dto.ApplePublicKeyResponse;
import com.ssh.dartserver.global.auth.dto.TokenResponse;
import com.ssh.dartserver.global.auth.infra.AppleOauthApi;
import com.ssh.dartserver.global.auth.service.jwt.JwtTokenProvider;
import com.ssh.dartserver.global.error.AppleLoginFailedException;
import com.ssh.dartserver.global.error.ApplePublicKeyNotFoundException;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@OauthProviderType(OauthProvider.APPLE)
public class AppleOauthService extends OauthServiceAbstract {
    private final AppleOauthApi appleOauthApi;

    public AppleOauthService(UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenProvider jwtTokenProvider, AppleOauthApi appleOauthApi) {
        super(userRepository, bCryptPasswordEncoder, jwtTokenProvider);
        this.appleOauthApi = appleOauthApi;
    }

    @Override
    public TokenResponse createToken(final String providerToken) {
        DecodedJWT jwt = JWT.decode(providerToken);

        try {
            AppleJwtToken appleJwtToken = new AppleJwtToken(jwt);
            ApplePublicKeyResponse applePublicKeys = appleOauthApi.getApplePublicKey();
            final Map<String, Object> possiblePayloadMap = appleJwtToken.getPossiblePayloadMap(applePublicKeys);
            OAuthUserInfo appleUser = new AppleUser(possiblePayloadMap);

            //apple username 검색을 통한 기존 유저 확인
            User userEntity = userRepository.findByUsername("apple_" + appleJwtToken.getSub())
                .orElse(null);

            return getTokenResponseDto(appleUser, userEntity);
        } catch (ApplePublicKeyNotFoundException | AppleLoginFailedException e) {
            throw e;
        } catch (Exception e) {
            throw new AppleLoginFailedException("유효하지 않은 Apple 토큰입니다", e);
        }
    }

    public static class AppleJwtToken {
        private final ObjectMapper objectMapper = new ObjectMapper();
        private final Base64.Decoder decoder = Base64.getDecoder();

        private final DecodedJWT decodedJWT;
        private final Map<String, Object> header;
        private final Map<String, Object> payload;

        public AppleJwtToken(DecodedJWT jwt) throws JsonProcessingException {
            decodedJWT = jwt;
            String headerString = new String(decoder.decode(jwt.getHeader()), StandardCharsets.UTF_8);
            String payloadString = new String(decoder.decode(jwt.getPayload()), StandardCharsets.UTF_8);

            header = objectMapper.readValue(headerString, Map.class);
            payload = objectMapper.readValue(payloadString, Map.class);
        }

        public Map<String, Object> getPossiblePayloadMap(ApplePublicKeyResponse applePublicKeys)
            throws JsonProcessingException {
            ApplePublicKey possibleApplePublicKey = applePublicKeys.getKeys().stream()
                .filter(key -> key.getKid().equals(getKid()))
                .findFirst()
                .orElseThrow(() -> new ApplePublicKeyNotFoundException("일치하는 Apple Public Key가 없습니다."));

            RSAPublicKey publicKey = getPublicKey(possibleApplePublicKey.getN(), possibleApplePublicKey.getE())
                .orElseThrow(() -> new ApplePublicKeyNotFoundException("일치하는 Apple Public Key가 없습니다."));

            Algorithm algorithm = Algorithm.RSA256(publicKey, null);
            String possiblePayload = new String(decoder.decode(JWT.require(algorithm)
                .build()
                .verify(decodedJWT)
                .getPayload()), StandardCharsets.UTF_8);
            return objectMapper.readValue(possiblePayload, Map.class);
        }

        private Optional<RSAPublicKey> getPublicKey(String modulusBase64, String exponentBase64) {
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

        public String getKid() {
            return String.valueOf(header.get("kid"));
        }

        public String getSub() {
            return String.valueOf(payload.get("sub"));
        }
    }
}
