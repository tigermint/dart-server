package com.ssh.dartserver.domain.chat.presentation;

import com.ssh.dartserver.domain.chat.presentation.request.ChatMessageRequest;
import com.ssh.dartserver.domain.chat.presentation.response.ChatMessageResponse;
import com.ssh.dartserver.domain.chat.application.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatMessageController {

    private final ChatMessageService chatMessageService;
    private final SimpMessagingTemplate simpMessagingTemplate;

    /**
     * 채팅 메시지 전송
     * /app/chat/rooms/{chatRoomId} - 채팅방의 메시지를 받을 주소, subscription할 주소
     * /topic/chat/rooms/{chatRoomId} - 채팅방의 메시지를 보낼 주소
     */

    @MessageMapping("/chat/rooms/{chatRoomId}") //메시지 해당 주소로 매핑(app/chat/rooms/{chatRoomId})
    public void sendMessage(@DestinationVariable Long chatRoomId, ChatMessageRequest request) {
        ChatMessageResponse response = chatMessageService.createChatMessage(request);
        simpMessagingTemplate.convertAndSend("/topic/chat/rooms/" + chatRoomId, response); //해당 주소로 메시지 모두 전송(/topic/chat/rooms/{chatRoomId})
    }

    @GetMapping("v1/chat/rooms/{chatRoomId}/messages")
    public ResponseEntity<Page<ChatMessageResponse>> listChatMessage(
            @PathVariable Long chatRoomId,
            @RequestParam(required = false, defaultValue = "0", value = "page") int page,
            @RequestParam(required = false, defaultValue = "createdTime", value = "criteria") String criteria){
        return ResponseEntity.ok().body(chatMessageService.listChatMessage(chatRoomId, page, criteria));
    }
}
