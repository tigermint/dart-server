package com.ssh.dartserver.domain.team;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.UserManager;
import com.ssh.dartserver.testing.IntegrationTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

// TODO 승열
@IntegrationTest
public abstract class UsersTeamApiTest extends ApiTest {
    @Autowired
    private UserManager userManager;

    @Disabled
    @Test
    void t1() {
        // TODO GET /v1/users/me/teams
        final String jwtToken = userManager.createTestUser().getJwtToken();
        throw new UnsupportedOperationException();
    }

    @Disabled
    @Test
    void t2() {
        // TODO GET /v1/users/me/teams/{teamId}
        final String jwtToken = userManager.createTestUser().getJwtToken();
        throw new UnsupportedOperationException();
    }

    @Disabled
    @Test
    void t3() {
        // TODO DELETE /v1/users/me/teams/{teamId}
        final String jwtToken = userManager.createTestUser().getJwtToken();
        throw new UnsupportedOperationException();
    }

    @Disabled
    @Test
    void t4() {
        // TODO PATCH /v1/users/me/teams/{teamId}
        final String jwtToken = userManager.createTestUser().getJwtToken();
        throw new UnsupportedOperationException();
    }
}
