package com.ssh.dartserver.domain.chat.service;

import com.ssh.dartserver.domain.chat.domain.*;
import com.ssh.dartserver.domain.chat.dto.ChatMessageRequest;
import com.ssh.dartserver.domain.chat.dto.ChatMessageResponse;
import com.ssh.dartserver.domain.chat.dto.mapper.ChatMessageMapper;
import com.ssh.dartserver.domain.chat.infra.ChatMessageRepository;
import com.ssh.dartserver.domain.chat.presentation.ChatRoomUserRepository;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.global.infra.notification.PlatformNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChatMessageService {
    private static final int PAGE_SIZE = 10;

    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatMessageRepository chatMessageRepository;

    private final ChatRoomUserService chatRoomUserService;

    private final ChatMessageMapper chatMessageMapper;

    private final PlatformNotification notification;

    @Transactional
    public ChatMessageResponse createChatMessage(ChatMessageRequest request) {

        //전체 방 인원 조회
        List<ChatRoomUser> chatRoomUsers = chatRoomUserRepository.findAllByChatRoomId(request.getChatRoomId());

        //sender가 채팅방에 속한 인원인지 판단
        ChatRoomUser chatRoomUser = chatRoomUsers.stream()
                .filter(s -> s.getUser().getId().equals(request.getSenderId()))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("sender가 해당 채팅방에 속한 인원이 아닙니다."));

        ChatRoom chatRoom = chatRoomUser.getChatRoom();
        User user = chatRoomUser.getUser();

        if (ChatMessageType.valueOf(request.getChatMessageType()).equals(ChatMessageType.QUIT)) {
            request.setContent(user.getPersonalInfo().getNickname().getValue() + "님이 나갔습니다.");
            chatRoomUserService.deleteChatRoomUser(request.getSenderId(), request.getChatRoomId());
        }

        //메시지 저장 로직
        ChatMessage chatMessage = ChatMessage.builder()
                .chatMessageType(ChatMessageType.valueOf(request.getChatMessageType()))
                .content(ChatContent.from(request.getContent()))
                .senderId(request.getSenderId())
                .chatRoom(chatRoomUser.getChatRoom())
                .build();

        ChatMessage savedChatMessage = chatMessageRepository.save(chatMessage);

        if (ChatMessageType.valueOf(request.getChatMessageType()).equals(ChatMessageType.TALK)) {
            sendChatMessageNotification(request, user, chatRoomUsers);
            chatRoom.updateLastMessage(savedChatMessage.getContent().getValue(), savedChatMessage.getCreatedTime());
        }
        return chatMessageMapper.toResponseDto(savedChatMessage);
    }

    public Page<ChatMessageResponse> listChatMessage(Long chatRoomId, int page, String criteria) {
        Pageable pageable = PageRequest.of(page, PAGE_SIZE, Sort.by(Sort.Direction.DESC, criteria));
        Page<ChatMessage> chatMessages = chatMessageRepository.findAllByChatRoomId(chatRoomId, pageable);
        return chatMessages.map(chatMessageMapper::toResponseDto);
    }

    private void sendChatMessageNotification(ChatMessageRequest request, User user, List<ChatRoomUser> chatRoomUsers) {

        //메시지 전송 알림
        String heading = user.getPersonalInfo().getNickname().getValue();
        String content = request.getContent();

        List<String> userIds = chatRoomUsers.stream()
                .map(ChatRoomUser::getUser)
                .map(User::getId)
                .filter(id -> !id.equals(request.getSenderId()))
                .map(String::valueOf)
                .collect(Collectors.toList());

        //TODO: 비동기 처리 필요
        CompletableFuture.runAsync(() ->
                notification.postNotificationSpecificDevice(userIds, heading, content)
        );
    }

}
