package com.ssh.dartserver.domain.chat.dto.mapper;

import com.ssh.dartserver.domain.chat.domain.ChatMessage;
import com.ssh.dartserver.domain.chat.dto.ChatMessageResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {
    @Mapping(target = "chatMessageId", source = "chatMessage.id")
    @Mapping(target = "chatMessageType", source = "chatMessage.chatMessageType")
    @Mapping(target = "content", source = "chatMessage.content.value")
    @Mapping(target = "senderId", source = "chatMessage.senderId")
    @Mapping(target = "chatRoomId", source = "chatMessage.chatRoom.id")
    @Mapping(target = "createdTime", source = "chatMessage.createdTime")
    ChatMessageResponse toResponseDto(ChatMessage chatMessage);
}
