package com.ssh.dartserver.domain.chat;

import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.UserManager;
import com.ssh.dartserver.domain.chat.presentation.response.ChatRoomResponse;
import com.ssh.dartserver.domain.region.RegionSteps;
import com.ssh.dartserver.domain.team.TeamSteps;
import com.ssh.dartserver.domain.team.infra.RegionRepository;
import com.ssh.dartserver.domain.university.UniversitySteps;
import com.ssh.dartserver.domain.university.infra.UniversityRepository;
import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import com.ssh.dartserver.testing.IntegrationTest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@IntegrationTest
public abstract class UsersChatRoomApiTest extends ApiTest {
    @Autowired
    private UserManager userManager;

    @Autowired
    private UniversityRepository universityRepository;

    @Autowired
    private RegionRepository regionRepository;

    static String jwtToken;

    @BeforeEach
    void setUp() {
        universityRepository.saveAll(UniversitySteps.대학생성요청_생성(3));
        regionRepository.saveAll(RegionSteps.지역생성요청_생성(3));
        jwtToken = userManager.createTestUserWithInformation(Gender.MALE).getJwtToken();

    }

    /**
     * GET /me/chat/rooms/{chatRoomId}
     */

    @DisplayName("chatRoomId로 채팅방을 조회하면, 200과 채팅방 정보를 반환한다.")
    @Test
    void test_readChatRoom_isSuccess() {
        //given
        final String requestedTeamUserJwtToken = userManager.createTestUserWithInformation(Gender.FEMALE).getJwtToken();

        final long requestingTeamId = TeamSteps.getCreatedTeamId(jwtToken);
        final long requestedTeamId = TeamSteps.getCreatedTeamId(requestedTeamUserJwtToken);

        final long chatRoomId = ChatRoomSteps.getCreatedChatRoomId(requestingTeamId, requestedTeamId, jwtToken);

        //when
        final ExtractableResponse<Response> extractableResponse = RestAssured
                .given().log().all()
                .auth().oauth2(jwtToken)
                .when()
                .get("/v1/users/me/chat/rooms/{chatRoomId}", chatRoomId)
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        //then
        final ChatRoomResponse.ReadDto readDto = extractableResponse.as(ChatRoomResponse.ReadDto.class);
        Assertions.assertThat(readDto.getChatRoomId()).isEqualTo(chatRoomId);
    }

    /**
     * GET /me/chat/rooms
     */
    @DisplayName("사용자가 참여한 모든 채팅방을 조회하면, 200과 채팅방 목록을 반환한다.")
    @Test
    void test_listChatRoom_isSuccess() {
        //given
        final String requestedTeam1UserJwtToken = userManager.createTestUserWithInformation(Gender.FEMALE).getJwtToken();
        final String requestedTeam2UserJwtToken = userManager.createTestUserWithInformation(Gender.FEMALE).getJwtToken();

        final long requestingTeamId = TeamSteps.getCreatedTeamId(jwtToken);
        final long requestedTeam1Id = TeamSteps.getCreatedTeamId(requestedTeam1UserJwtToken);
        final long requestedTeam2Id = TeamSteps.getCreatedTeamId(requestedTeam2UserJwtToken);

        final long chatRoom1Id = ChatRoomSteps.getCreatedChatRoomId(requestingTeamId, requestedTeam1Id, jwtToken);
        final long chatRoom2Id = ChatRoomSteps.getCreatedChatRoomId(requestingTeamId, requestedTeam2Id, jwtToken);

        //when
        final ExtractableResponse<Response> extractableResponse = RestAssured
                .given().log().all()
                .auth().oauth2(jwtToken)
                .when()
                .get("/v1/users/me/chat/rooms")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        final ChatRoomResponse.ListDto[] listDtos = extractableResponse.as(ChatRoomResponse.ListDto[].class);

        //then
        Assertions.assertThat(listDtos).hasSize(2);
        List<Long> chatRoomIds = Arrays.stream(listDtos)
                .map(ChatRoomResponse.ListDto::getChatRoomId)
                .collect(Collectors.toList());

        Assertions.assertThat(chatRoomIds).containsExactlyInAnyOrder(chatRoom1Id, chatRoom2Id);
    }
}
