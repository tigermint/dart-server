package com.ssh.dartserver.domain.proposal;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.UserManager;
import com.ssh.dartserver.domain.chat.UsersChatApiTest;
import com.ssh.dartserver.testing.IntegrationTest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

// TODO 현식
@IntegrationTest
public class ProposalApiTest  extends ApiTest {
    @Autowired
    private UserManager userManager;

    @Nested
    class UsersTest extends UsersProposalApiTest {}

    @Disabled
    @Test
    void t() {
        // TODO POST /v1/proposals
        final String jwtToken = userManager.createTestUser().getJwtToken();
        throw new UnsupportedOperationException();
    }
}
