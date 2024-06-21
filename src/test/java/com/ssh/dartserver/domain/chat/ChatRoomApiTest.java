package com.ssh.dartserver.domain.chat;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.UserManager;
import com.ssh.dartserver.domain.region.RegionSteps;
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
import org.junit.jupiter.api.Nested;
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

    static String jwtToken;

    @BeforeEach
    void setUp() {
        universityRepository.saveAll(UniversitySteps.대학생성요청_생성(3));
        regionRepository.saveAll(RegionSteps.지역생성요청_생성(3));
        jwtToken = userManager.createTestUserWithInformation(Gender.MALE).getJwtToken();
    }

    @Nested
    class UsersTest extends UsersChatRoomApiTest {}


    /**
     * POST /v1/chat/rooms
     */
    @DisplayName("호감 아이디를 받아 채팅방을 생성하면, 201과 URI 주소를 반환한다.")
    @Test
    void test_listChatMessage_isSuccess() {
        //given
        final String requestedTeamUserJwtToken = userManager.createTestUserWithInformation(Gender.FEMALE).getJwtToken();

        final long requestingTeamId = TeamSteps.getCreatedTeamId(jwtToken);
        final long requestedTeamId = TeamSteps.getCreatedTeamId(requestedTeamUserJwtToken);

        //when
        final ExtractableResponse<Response> extractableResponse = ChatRoomSteps.채팅방_생성(requestingTeamId, requestedTeamId, jwtToken);

        //then
        assertThat(extractableResponse.statusCode()).isEqualTo(HttpStatus.CREATED.value());
    }
}
