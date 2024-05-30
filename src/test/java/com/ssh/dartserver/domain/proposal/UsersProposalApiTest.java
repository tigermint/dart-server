package com.ssh.dartserver.domain.proposal;

import static com.ssh.dartserver.domain.proposal.ProposalSteps.받은호감조회요청;
import static com.ssh.dartserver.domain.proposal.ProposalSteps.보낸호감조회요청;
import static com.ssh.dartserver.domain.proposal.ProposalSteps.호감거절요청_생성;
import static com.ssh.dartserver.domain.proposal.ProposalSteps.호감보내기요청;
import static com.ssh.dartserver.domain.proposal.ProposalSteps.호감보내기요청_생성;
import static com.ssh.dartserver.domain.proposal.ProposalSteps.호감수락거절요청;
import static com.ssh.dartserver.domain.proposal.ProposalSteps.호감수락요청_생성;
import static org.assertj.core.api.Assertions.*;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.UserManager;
import com.ssh.dartserver.domain.proposal.domain.ProposalStatus;
import com.ssh.dartserver.domain.proposal.dto.ProposalRequest.Create;
import com.ssh.dartserver.domain.proposal.dto.ProposalRequest.Update;
import com.ssh.dartserver.domain.proposal.dto.ProposalResponse.ListDto;
import com.ssh.dartserver.domain.proposal.dto.ProposalResponse.UpdateDto;
import com.ssh.dartserver.testing.IntegrationTest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

@IntegrationTest
public abstract class UsersProposalApiTest extends ApiTest {
    @Autowired
    private UserManager userManager;
    @Autowired
    private ProposalTestHelper helper;

    // PATCH /v1/users/me/proposals/{proposalId} - 수락
    @Test
    void 호감수락하기() {
        helper.대학생성(10);
        final String user1Token = userManager.createTestUserWithInformation().getJwtToken();  // 1번인간 (나)
        final String user2Token = userManager.createTestUserWithInformation().getJwtToken();  // 2번인간
        helper.지역생성(10);
        helper.팀만들기(1L);
        helper.팀만들기(2L);
        final Create request = 호감보내기요청_생성(1, 2);
        final Update proposalRequest = 호감수락요청_생성();

        호감보내기요청(user1Token, request);
        final ExtractableResponse<Response> response = 호감수락거절요청(user2Token, 1L, proposalRequest);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final UpdateDto updateDto = response.body().as(UpdateDto.class);
        assertThat(updateDto.getProposalStatus()).isEqualTo(ProposalStatus.PROPOSAL_SUCCESS.toString());
    }

    // PATCH /v1/users/me/proposals/{proposalId} - 수락
    @Test
    void 호감거절하기() {
        helper.대학생성(10);
        final String user1Token = userManager.createTestUserWithInformation().getJwtToken();  // 1번인간 (나)
        final String user2Token = userManager.createTestUserWithInformation().getJwtToken();  // 2번인간
        helper.지역생성(10);
        helper.팀만들기(1L);
        helper.팀만들기(2L);
        final Create request = 호감보내기요청_생성(1, 2);
        final Update proposalRequest = 호감거절요청_생성();

        호감보내기요청(user1Token, request);
        final ExtractableResponse<Response> response = 호감수락거절요청(user2Token, 1L, proposalRequest);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final UpdateDto updateDto = response.body().as(UpdateDto.class);
        assertThat(updateDto.getProposalStatus()).isEqualTo(ProposalStatus.PROPOSAL_FAILED.toString());
    }

    // GET /v1/users/me/proposals
    @Test
    void 보낸호감_목록조회하기() {
        helper.대학생성(10);
        final String user1Token = userManager.createTestUserWithInformation().getJwtToken();  // 1번인간 (나)
        final String user2Token = userManager.createTestUserWithInformation().getJwtToken();  // 2번인간
        helper.지역생성(10);
        helper.팀만들기(1L);
        helper.팀만들기(2L);
        final Create request = 호감보내기요청_생성(1, 2);

        호감보내기요청(user1Token, request);
        final ExtractableResponse<Response> response = 보낸호감조회요청(user1Token);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final ListDto[] listDto = response.body().as(ListDto[].class);
        assertThat(listDto.length).isEqualTo(1);
    }

    // GET /v1/users/me/proposals?type=received
    @Test
    void 받은호감_목록조회하기() {
        helper.대학생성(10);
        final String user1Token = userManager.createTestUserWithInformation().getJwtToken();  // 1번인간 (나)
        final String user2Token = userManager.createTestUserWithInformation().getJwtToken();  // 2번인간
        helper.지역생성(10);
        helper.팀만들기(1L);
        helper.팀만들기(2L);
        final Create request = 호감보내기요청_생성(1, 2);

        호감보내기요청(user1Token, request);
        final ExtractableResponse<Response> response = 받은호감조회요청(user2Token);

        assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
        final ListDto[] listDto = response.body().as(ListDto[].class);
        assertThat(listDto.length).isEqualTo(1);
    }
}
