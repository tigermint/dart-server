package com.ssh.dartserver.domain.chatRoom;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.UserManager;
import com.ssh.dartserver.domain.chat.dto.ChatRoomRequest;
import com.ssh.dartserver.domain.proposal.ProposalSteps;
import com.ssh.dartserver.domain.proposal.dto.ProposalRequest;
import com.ssh.dartserver.domain.region.RegionSteps;
import com.ssh.dartserver.domain.team.TeamRequestTestFixture;
import com.ssh.dartserver.domain.team.TeamSteps;
import com.ssh.dartserver.domain.team.infra.RegionRepository;
import com.ssh.dartserver.domain.university.UniversitySteps;
import com.ssh.dartserver.domain.university.infra.UniversityRepository;
import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import com.ssh.dartserver.testing.IntegrationTest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

@IntegrationTest
class ChatRoomApiTest extends ApiTest {
    @Autowired
    private UniversityRepository universityRepository;
    @Autowired
    private RegionRepository regionRepository;
    @Autowired
    private UserManager userManager;

    @BeforeEach
    void setUp() {
        universityRepository.saveAll(UniversitySteps.대학생성요청_생성(3));
        regionRepository.saveAll(RegionSteps.지역생성요청_생성(3));
    }

    /**
     * POST /v1/chat/rooms
     */
    @DisplayName("호감 아이디를 받아 채팅방을 생성하면, 201과 URI 주소를 반환한다.")
    @Test
    void test_listChatMessage_isSuccess() {
        //given
        final String maleUserJwtToken = userManager.createTestUserWithInformation(Gender.MALE).getJwtToken();
        final long requestingTeamId = Long.parseLong(getCreatedTeamId(maleUserJwtToken));

        final String femaleUserJwtToken = userManager.createTestUserWithInformation(Gender.FEMALE).getJwtToken();
        final long requestedTeamId = Long.parseLong(getCreatedTeamId(femaleUserJwtToken));

        final String proposalId = getCreatedProposalId(requestingTeamId, requestedTeamId, maleUserJwtToken);

        ChatRoomRequest.Create chatRoomRequest = new ChatRoomRequest.Create();
        chatRoomRequest.setProposalId(Long.parseLong(proposalId));

        //when
        final ExtractableResponse<Response> extractableResponse = ChatRoomSteps.채팅방_생성(maleUserJwtToken, chatRoomRequest);

        //then
        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }

    private static String getCreatedProposalId(final long createdMaleTeamId, final long createdFemaleTeamId, final String maleUserJwtToken) {
        final ProposalRequest.Create proposalRequest = ProposalSteps.호감보내기요청_생성(createdMaleTeamId, createdFemaleTeamId);
        final ExtractableResponse<Response> extractableResponse = ProposalSteps.호감보내기요청(maleUserJwtToken, proposalRequest);
        final String location = extractableResponse.header("Location");
        return location.substring(location.lastIndexOf("/") + 1);
    }


    private String getCreatedTeamId(String jwtToken) {
        final ExtractableResponse<Response> extractableResponse = TeamSteps.팀_생성(jwtToken, TeamRequestTestFixture.getTeamRequest());
        final String location = extractableResponse.header("Location");
        return location.substring(location.lastIndexOf("/") + 1);
    }
}
