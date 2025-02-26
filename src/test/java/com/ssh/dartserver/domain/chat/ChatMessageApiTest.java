package com.ssh.dartserver.domain.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.ssh.dartserver.ApiTest;
import com.ssh.dartserver.UserManager;
import com.ssh.dartserver.domain.chat.domain.ChatMessageType;
import com.ssh.dartserver.domain.chat.presentation.request.ChatMessageRequest;
import com.ssh.dartserver.domain.chat.presentation.response.ChatMessageResponse;
import com.ssh.dartserver.domain.region.RegionSteps;
import com.ssh.dartserver.domain.team.TeamSteps;
import com.ssh.dartserver.domain.team.infra.RegionRepository;
import com.ssh.dartserver.domain.university.UniversitySteps;
import com.ssh.dartserver.domain.university.infra.UniversityRepository;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import com.ssh.dartserver.global.config.properties.JwtProperty;
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
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@IntegrationTest
class ChatMessageApiTest extends ApiTest {

    @Autowired
    private UserManager userManager;

    @Autowired
    private UniversityRepository universityRepository;

    @Autowired
    private RegionRepository regionRepository;

    private BlockingQueue<ChatMessageResponse> blockingQueue;
    private WebSocketStompClient stompClient;
    static String jwtToken;

    @BeforeEach
    void setUp() {
        universityRepository.saveAll(UniversitySteps.대학생성요청_생성(3));
        regionRepository.saveAll(RegionSteps.지역생성요청_생성(3));
        jwtToken = userManager.createTestUserWithInformation(Gender.MALE).getJwtToken();

        blockingQueue = new LinkedBlockingQueue<>();
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setObjectMapper(objectMapper);

        stompClient.setMessageConverter(messageConverter);
    }

    /**
     * MESSAGE /app/chat/rooms/{chatRoomId}
     *
     * @throws Exception
     */
    @DisplayName("WebSocket을 통해 채팅 메시지를 전송하고 수신한다.")
    @Test
    void test_sendMessage_isSuccess() throws Exception {
        // given
        final User user = userManager.getUser(jwtToken);

        final String requestedTeamUserJwtToken = userManager.createTestUserWithInformation(Gender.FEMALE).getJwtToken();
        final long createdRequestingTeamId = TeamSteps.getCreatedTeamId(jwtToken);
        final long createdRequestedTeamId = TeamSteps.getCreatedTeamId(requestedTeamUserJwtToken);
        final long createdChatRoomId = ChatRoomSteps.getCreatedChatRoomId(createdRequestingTeamId, createdRequestedTeamId, jwtToken);

        // when
        sendMessageToChatRoom(createdChatRoomId, jwtToken, user, "Hello, World!");

        // then
        final ChatMessageResponse response = blockingQueue.poll(10, TimeUnit.SECONDS);

        Assertions.assertThat(response).isNotNull();
        Assertions.assertThat(response.getChatRoomId()).isEqualTo(createdChatRoomId);
        Assertions.assertThat(response.getSenderId()).isEqualTo(user.getId());
        Assertions.assertThat(response.getContent()).isEqualTo("Hello, World!");
    }

    /**
     * GET /v1/chat/rooms/{chatRoomId}/messages
     *
     * @throws Exception
     */

    @DisplayName("채팅방의 모든 메시지를 조회한다.")
    @Test
    void test_listChatMessage_isSuccess() throws Exception {
        // given
        final String requestedTeamUserJwtToken = userManager.createTestUserWithInformation(Gender.FEMALE).getJwtToken();

        final User requestingUser = userManager.getUser(jwtToken);
        final User requestedUser = userManager.getUser(requestedTeamUserJwtToken);

        final long requestingTeamId = TeamSteps.getCreatedTeamId(jwtToken);
        final long requestedTeamId = TeamSteps.getCreatedTeamId(requestedTeamUserJwtToken);
        final long chatRoomId = ChatRoomSteps.getCreatedChatRoomId(requestingTeamId, requestedTeamId, jwtToken);


        // 채팅 메시지 전송
        final CompletableFuture<Void> future1 = sendMessageToChatRoom(chatRoomId, jwtToken, requestingUser, "Hello, World!");
        final CompletableFuture<Void> future2 = sendMessageToChatRoom(chatRoomId, requestedTeamUserJwtToken, requestedUser, "Hi there!");

        CompletableFuture.allOf(future1, future2).get(10, TimeUnit.SECONDS);

        // when
        final ExtractableResponse<Response> extractableResponse =
                RestAssured
                        .given()
                        .auth().oauth2(jwtToken)
                        .when()
                        .get("/v1/chat/rooms/{chatRoomId}/messages", chatRoomId)
                        .then()
                        .statusCode(HttpStatus.OK.value())
                        .extract();

        // then
        final List<ChatMessageResponse> messages = extractableResponse
                .jsonPath()
                .getList("content", ChatMessageResponse.class);

        Assertions.assertThat(messages)
                .isNotEmpty()
                .hasSize(2);
        List<String> messageContents = messages.stream()
                .map(ChatMessageResponse::getContent)
                .collect(Collectors.toList());

        Assertions.assertThat(messageContents)
                .containsExactlyInAnyOrder("Hello, World!", "Hi there!");
    }

    private CompletableFuture<Void> sendMessageToChatRoom(long chatRoomId, String jwtToken, User user, String content) throws Exception {
        final StompSession stompSession = connectWebSocket(jwtToken);
        CompletableFuture<Void> future = new CompletableFuture<>();
        subscribeToChatRoom(stompSession, chatRoomId, future);
        sendMessage(stompSession, chatRoomId, user, content);
        return future;
    }

    private StompSession connectWebSocket(final String jwtToken) throws Exception {
        final StompHeaders stompHeaders = new StompHeaders();
        stompHeaders.add(JwtProperty.HEADER_STRING, JwtProperty.TOKEN_PREFIX + jwtToken);

        ListenableFuture<StompSession> connect = stompClient.connect(
                getWebSocketUri(),
                new WebSocketHttpHeaders(),
                stompHeaders,
                new StompSessionHandlerAdapter() {
                }
        );
        return connect.get(10, TimeUnit.SECONDS);
    }


    private void subscribeToChatRoom(final StompSession stompSession, final long chatRoomId, CompletableFuture<Void> future) {
        stompSession.subscribe("/topic/chat/rooms/" + chatRoomId, new StompFrameHandlerImpl<>(new ChatMessageResponse(), blockingQueue){
            @Override
            public void handleFrame(final StompHeaders headers, final Object payload) {
                super.handleFrame(headers, payload);
                future.complete(null);
            }
        });
    }

    private static void sendMessage(final StompSession stompSession, final long chatRoomId, final User user, String content) {
        final ChatMessageRequest chatMessageRequest = getChatMessageRequest(chatRoomId, user, content);
        stompSession.send("/app/chat/rooms/" + chatRoomId, chatMessageRequest);
    }

    private static ChatMessageRequest getChatMessageRequest(final long chatRoomId, final User user, String content) {
        final ChatMessageRequest chatMessageRequest = new ChatMessageRequest();
        chatMessageRequest.setChatMessageType(ChatMessageType.TALK.getValue());
        chatMessageRequest.setChatRoomId(chatRoomId);
        chatMessageRequest.setSenderId(user.getId());
        chatMessageRequest.setContent(content);
        return chatMessageRequest;
    }

    private static String getWebSocketUri() {
        return "ws://localhost:" + RestAssured.port + "/v1/ws";
    }
}
