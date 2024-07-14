package com.ssh.dartserver.domain.chat.presentation;

import com.ssh.dartserver.domain.chat.dto.ChatRoomRequest;
import com.ssh.dartserver.domain.chat.dto.ChatRoomResponse;
import com.ssh.dartserver.domain.chat.service.ChatRoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/chat/rooms")
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    @PostMapping
    public ResponseEntity<ChatRoomResponse> createRoom(@RequestBody ChatRoomRequest.Create request) {
        Long chatRoomId = chatRoomService.createChatRoom(request);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(chatRoomId)
                .toUri();
        return ResponseEntity.created(location).build();
    }
}
