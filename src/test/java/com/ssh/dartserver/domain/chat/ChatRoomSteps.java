package com.ssh.dartserver.domain.chat;

import com.ssh.dartserver.domain.chat.presentation.request.ChatRoomRequest;
import com.ssh.dartserver.domain.proposal.ProposalSteps;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
public class ChatRoomSteps {

    public static ExtractableResponse<Response> 채팅방_생성(final long requestingTeamId, final long requestedTeamId, final String requestingTeamUserJwtToken) {

        final String proposalId = ProposalSteps.getCreatedProposalId(requestingTeamId, requestedTeamId, requestingTeamUserJwtToken);

        ChatRoomRequest.Create chatRoomRequest = new ChatRoomRequest.Create();
        chatRoomRequest.setProposalId(Long.parseLong(proposalId));

        return RestAssured
                .given().log().all()
                .auth().oauth2(requestingTeamUserJwtToken)
                .contentType("application/json")
                .body(chatRoomRequest)
                .when()
                .post("/v1/chat/rooms")
                .then().log().all()
                .statusCode(201)
                .extract();

    }

    public static long getCreatedChatRoomId(final long requestingTeamId, final long requestedTeamId, final String requestingTeamUserJwtToken) {
        final ExtractableResponse<Response> chatRoomResponse = ChatRoomSteps.채팅방_생성(requestingTeamId, requestedTeamId, requestingTeamUserJwtToken);
        final String location = chatRoomResponse.header("Location");
        return Long.parseLong(location.substring(location.lastIndexOf("/") + 1));
    }
}
