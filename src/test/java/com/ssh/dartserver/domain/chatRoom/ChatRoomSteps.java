package com.ssh.dartserver.domain.chatRoom;

import com.ssh.dartserver.domain.chat.dto.ChatRoomRequest;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

public class ChatRoomSteps {
    public static ExtractableResponse<Response> 채팅방_생성(final String requestingTeamUserJwtToken, final ChatRoomRequest.Create chatRoomRequest) {
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
}
