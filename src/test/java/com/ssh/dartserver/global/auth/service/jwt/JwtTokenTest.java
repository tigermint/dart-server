package com.ssh.dartserver.global.auth.service.jwt;

import static org.junit.jupiter.api.Assertions.*;

import com.auth0.jwt.exceptions.JWTDecodeException;
import com.ssh.dartserver.domain.user.domain.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class JwtTokenTest {
    @Test
    @DisplayName("유저 객체를 기준으로 JWT토큰을 생성한다.")
    public void test_token_creation_with_valid_user_data() {
        User user = User.builder()
            .username("testuser")
            .id(1L)
            .build();

        JwtToken jwtToken = JwtToken.create(user);

        assertNotNull(jwtToken);
        assertEquals("testuser", jwtToken.getUsername());
    }

    @Test
    @DisplayName("유효하지 않은 토큰 생성시 JWTDecodeException 예외가 발생한다.")
    public void test_extracting_username_from_malformed_token() {
        String malformedToken = "malformed.token.string";

        assertThrows(JWTDecodeException.class, () -> {
            new JwtToken(malformedToken).getUsername();
        });
    }
}