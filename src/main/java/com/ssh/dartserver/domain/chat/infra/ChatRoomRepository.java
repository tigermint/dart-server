package com.ssh.dartserver.domain.chat.infra;

import com.ssh.dartserver.domain.chat.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
