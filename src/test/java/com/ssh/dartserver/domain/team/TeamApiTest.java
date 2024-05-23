package com.ssh.dartserver.domain.team;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.UserManager;
import com.ssh.dartserver.domain.chat.UsersChatApiTest;
import com.ssh.dartserver.testing.IntegrationTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

// TODO 승열
@IntegrationTest
public class TeamApiTest  extends ApiTest {
    @Autowired
    private UserManager userManager;

    @Nested
    class UsersTest extends UsersTeamApiTest {}

    @Disabled
    @Test
    void t1() {
        // TODO GET /v1/teams
        final String jwtToken = userManager.createTestUser().getJwtToken();
        throw new UnsupportedOperationException();
    }

    @Disabled
    @Test
    void t2() {
        // TODO POST /v1/teams
        final String jwtToken = userManager.createTestUser().getJwtToken();
        throw new UnsupportedOperationException();
    }

    @Disabled
    @Test
    void t3() {
        // TODO GET /v1/teams/{id}
        final String jwtToken = userManager.createTestUser().getJwtToken();
        throw new UnsupportedOperationException();
    }

    @Disabled
    @Test
    void t4() {
        // TODO GET /v1/teams/count
        final String jwtToken = userManager.createTestUser().getJwtToken();
        throw new UnsupportedOperationException();
    }
}
