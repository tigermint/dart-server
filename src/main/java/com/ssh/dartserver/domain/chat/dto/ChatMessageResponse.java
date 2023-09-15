package com.ssh.dartserver.domain.chat.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ChatMessageResponse {
    private String chatMessageType;
    private Long chatMessageId;
    private Long senderId;
    private Long chatRoomId;
    private String content;
    private LocalDateTime createdTime;
}