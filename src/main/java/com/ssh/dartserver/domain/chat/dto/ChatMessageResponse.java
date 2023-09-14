package com.ssh.dartserver.domain.chat.dto;

import lombok.Data;

import java.time.LocalDateTime;
@Data
public class ChatMessageResponse {
    private Long chatMessageId;
    private Long senderId;
    private Long chatRoomId;
    private String chatMessageType;
    private String content;
    private LocalDateTime createdTime;
}