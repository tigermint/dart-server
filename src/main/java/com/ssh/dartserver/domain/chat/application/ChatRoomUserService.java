package com.ssh.dartserver.domain.chat.application;

import com.ssh.dartserver.domain.chat.infra.ChatRoomUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatRoomUserService {
    private final ChatRoomUserRepository chatRoomUserRepository;

    @Transactional
    public void deleteChatRoomUser(Long userId, Long chatRoomId) {
        chatRoomUserRepository.deleteByUserIdAndChatRoomId(userId, chatRoomId);
    }
}
