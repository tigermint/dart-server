package com.ssh.dartserver.domain.proposal;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.UserManager;
import com.ssh.dartserver.domain.proposal.presentation.request.ProposalRequest.Create;
import com.ssh.dartserver.testing.IntegrationTest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static com.ssh.dartserver.domain.proposal.ProposalSteps.호감보내기요청;
import static com.ssh.dartserver.domain.proposal.ProposalSteps.호감보내기요청_생성;
import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class ProposalApiTest  extends ApiTest {
    @Autowired
    private UserManager userManager;
    @Autowired
    private ProposalTestHelper helper;

    @Nested
    class UsersTest extends UsersProposalApiTest {}

    // POST /v1/proposals
    @Test
    void 호감보내기() {
        helper.대학생성(10);
        final String jwtToken = userManager.createTestUserWithInformation().getJwtToken();  // 1번인간 (나)
        userManager.createTestUserWithInformation().getJwtToken();  // 2번인간
        helper.지역생성(10);
        helper.팀만들기(1L);
        helper.팀만들기(2L);
        final Create request = 호감보내기요청_생성(1, 2);

        final ExtractableResponse<Response> response = 호감보내기요청(jwtToken, request);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }
}
