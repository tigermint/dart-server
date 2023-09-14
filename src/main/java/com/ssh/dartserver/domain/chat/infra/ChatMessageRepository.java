package com.ssh.dartserver.domain.chat.infra;

import com.ssh.dartserver.domain.chat.domain.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    @Query("select cm from ChatMessage cm where cm.chatRoom.id = :chatRoomId")
    Page<ChatMessage> findAllByChatRoomId(@Param("chatRoomId") Long chatRoomId, Pageable pageable);
}
