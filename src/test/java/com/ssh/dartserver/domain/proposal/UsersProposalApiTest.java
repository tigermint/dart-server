package com.ssh.dartserver.domain.proposal;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.UserManager;
import com.ssh.dartserver.testing.IntegrationTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

// TODO 현식
@IntegrationTest
public abstract class UsersProposalApiTest extends ApiTest {
    @Autowired
    private UserManager userManager;

    @Disabled
    @Test
    void t1() {
        // TODO PATCH /v1/users/me/proposals/{proposalId}
        final String jwtToken = userManager.createTestUser().getJwtToken();
        throw new UnsupportedOperationException();
    }

    @Disabled
    @Test
    void t() {
        // TODO GET /v1/users/me/proposals
        final String jwtToken = userManager.createTestUser().getJwtToken();
        throw new UnsupportedOperationException();
    }
}
