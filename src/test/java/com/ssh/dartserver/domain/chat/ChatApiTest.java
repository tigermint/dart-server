package com.ssh.dartserver.domain.chat;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.UserManager;
import com.ssh.dartserver.testing.IntegrationTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

// TODO 승열
@IntegrationTest
public class ChatApiTest extends ApiTest {
    @Autowired
    private UserManager userManager;

    @Nested
    class UsersTest extends UsersChatApiTest {}

    @Disabled
    @Test
    void t1() {
        // TODO POST /v1/chat/rooms
        final String jwtToken = userManager.createTestUser().getJwtToken();
        throw new UnsupportedOperationException();
    }

    @Disabled
    @Test
    void t2() {
        // TODO GET /v1/chat/rooms/{chatRoomId}/messages
        final String jwtToken = userManager.createTestUser().getJwtToken();
        throw new UnsupportedOperationException();
    }
}
