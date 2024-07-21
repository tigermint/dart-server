package com.ssh.dartserver.global.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.infra.UserRepository;
import com.ssh.dartserver.global.auth.dto.ApplePublicKey;
import com.ssh.dartserver.global.auth.dto.ApplePublicKeyResponse;
import com.ssh.dartserver.global.auth.dto.TokenResponse;
import com.ssh.dartserver.global.auth.infra.AppleOauthApi;
import com.ssh.dartserver.global.auth.service.jwt.JwtToken;
import com.ssh.dartserver.global.auth.service.jwt.JwtTokenProvider;
import com.ssh.dartserver.global.error.AppleLoginFailedException;
import com.ssh.dartserver.global.error.ApplePublicKeyNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppleOauthServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private AppleOauthApi appleOauthApi;
    @InjectMocks
    private AppleOauthService appleOauthService;

    @Test
    @DisplayName("Apple 토큰을 검증하고 정상적으로 JWT를 생성한다.")
    void createToken_WithNewUser_ShouldCreateNewUserAndReturnToken() throws Exception {
        // Given
        String providerId = "apple_1234";
        KeyPair keyPair = generateKeyPair();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        String kid = "testKid";

        String appleIdToken = createAppleIdToken(kid, providerId, publicKey, privateKey);

        ApplePublicKeyResponse applePublicKeyResponse = createApplePublicKeyResponse(kid, publicKey);

        when(appleOauthApi.getApplePublicKey()).thenReturn(applePublicKeyResponse);
        when(userRepository.findByUsername("apple_" + providerId)).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(bCryptPasswordEncoder.encode(any())).thenReturn("encodedPassword");

        Date expiresAt = new Date(System.currentTimeMillis() + 1000 * 60 * 10);
        String jwtToken = createJwtToken("user", expiresAt);
        JwtToken jwtToken1 = new JwtToken(JWT.decode(jwtToken));
        when(jwtTokenProvider.create(any(User.class))).thenReturn(jwtToken1);

        // When
        TokenResponse tokenResponse = appleOauthService.createToken(appleIdToken);

        // Then
        assertNotNull(tokenResponse);
        assertEquals(jwtToken, tokenResponse.getJwtToken());
        assertEquals(providerId, tokenResponse.getProviderId());
        assertEquals("BEARER", tokenResponse.getTokenType().toUpperCase());
        assertEquals("apple", tokenResponse.getProviderType().toLowerCase());

        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Apple Public Key를 찾을 수 없는 경우 ApplePublicKeyNotFoundException이 발생한다.")
    void createToken_WhenApplePublicKeyNotFound_ShouldThrowException() throws Exception {
        // Given
        KeyPair keyPair = generateKeyPair();
        String appleIdToken = createAppleIdToken("unknownKid", "apple_1234", (RSAPublicKey) keyPair.getPublic(), (RSAPrivateKey) keyPair.getPrivate());
        when(appleOauthApi.getApplePublicKey()).thenReturn(new ApplePublicKeyResponse(Collections.emptyList()));

        // When & Then
        assertThrows(ApplePublicKeyNotFoundException.class, () -> appleOauthService.createToken(appleIdToken));
    }

    @Test
    @DisplayName("규격에 맞지 않는 JWT 토큰인 경우 JWTDecodeException이 발생한다.")
    void createToken_WithInvalidToken_ShouldThrowException() {
        String invalidToken = "invalid_token";

        assertThrows(JWTDecodeException.class, () -> appleOauthService.createToken(invalidToken));
    }

    @Test
    @DisplayName("규격에 맞지 않는 Apple 토큰인 경우 AppleLoginFailedExcpetion이 발생한다.")
    void createToken_WithInvalidToken_ShouldThrowException2() {
        final String fakeAppleToken = "eyJhbGciOiJSUzI1NiIsImtpZCI6ImVYdW5tTCJ9.eyJpc3MiOiJodHRwczovL2FwcGxlaWQuYXBwbGUuY29tIiwiYXVkIjoiY29tLnlvdXIuYXBwIiwiZXhwIjoxNjU5NjM4NDAwLCJpYXQiOjE2NTk1NDg0MDAsInN1YiI6IjAwMDEyMy5hYmNkZWZnLmhpamtsbW4iLCJub25jZSI6InJhbmRvbS1ub25jZS12YWx1ZSIsIm5vbmNlX3N1cHBvcnRlZCI6dHJ1ZSwiZW1haWwiOiJ1c2VyQGV4YW1wbGUuY29tIiwiZW1haWxfdmVyaWZpZWQiOiJ0cnVlIiwiaXNfcHJpdmF0ZV9lbWFpbCI6InRydWUiLCJhdXRoX3RpbWUiOjE2NTk1NDgzMDB9.b5Kt7k9yOJ3HYtvz_Y84lFAhG_I5F4ZTz9c4H3a-gt_cHc-K73fA1VxCJrQnpB5lt0NfiSmB0ziM-h0AzXhDO5Jw7E-Crjq_zV1myCvAnRMPHLRPTRjz_9w8llbmn1GgYLMNLpxiXqz34FcpQGBMdTCIZn4DiPBVfQNy-s63Jp1hGdsAd2qFwv6OcfAsVw3ZUy-MlgN_T7K7e_x4oGe7NTD6PKieZ5oZdQLTEkD8S-mQj4ybLSzxEVuVvI0i9iV6r_o5EJ3T6M12xh8GD1J-3F7KMe-NuUpaJvcI5gJpGbGv4mK1WBiCk2eUkZyJgDJi3mk1x6wGE5XJlY-ZO-xQf_w";

        assertThrows(AppleLoginFailedException.class, () -> appleOauthService.createToken(fakeAppleToken));
    }

    private KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);
        return keyPairGenerator.generateKeyPair();
    }

    private String createAppleIdToken(String kid, String sub, RSAPublicKey publicKey, RSAPrivateKey privateKey) {
        Algorithm algorithm = Algorithm.RSA256(publicKey, privateKey);
        return JWT.create()
            .withKeyId(kid)
            .withSubject(sub)
            .withIssuer("https://appleid.apple.com")
            .withExpiresAt(new Date(System.currentTimeMillis() + 1000 * 60 * 10))
            .sign(algorithm);
    }

    private ApplePublicKeyResponse createApplePublicKeyResponse(String kid, RSAPublicKey publicKey) {
        ApplePublicKey applePublicKey = new ApplePublicKey(
            "RSA",
            kid,
            "sig",
            "RS256",
            Base64.getUrlEncoder().encodeToString(publicKey.getModulus().toByteArray()),
            Base64.getUrlEncoder().encodeToString(publicKey.getPublicExponent().toByteArray())
        );
        return new ApplePublicKeyResponse(Collections.singletonList(applePublicKey));
    }

    private static String createJwtToken(final String tokenValue, Date expiresAt) {
        Algorithm algorithm = Algorithm.HMAC256("secret");
        return JWT.create()
            .withClaim(JwtToken.USERNAME_CLAIM, tokenValue)
            .withExpiresAt(expiresAt)
            .sign(algorithm);
    }
}
