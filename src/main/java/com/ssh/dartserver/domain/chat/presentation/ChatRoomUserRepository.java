package com.ssh.dartserver.domain.chat.presentation;

import com.ssh.dartserver.domain.chat.domain.ChatRoomUser;
import com.ssh.dartserver.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {
    List<ChatRoomUser> findAllByUser(User user);

    List<ChatRoomUser> findAllByChatRoomId(Long chatRoomId);

    void deleteByUserIdAndChatRoomId(Long userId, Long chatRoomId);
}
