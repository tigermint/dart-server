package com.ssh.dartserver.domain.chat.domain;

import com.ssh.dartserver.domain.proposal.domain.Proposal;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoom {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    @Column(name = "latest_chat_message_content")
    private String latestChatMessageContent;

    @Column(name = "latest_chat_message_time")
    private LocalDateTime latestChatMessageTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Proposal proposal;

    public void updateLastMessage(String latestChatMessageContent, LocalDateTime latestChatMessageTime) {
        this.latestChatMessageContent = latestChatMessageContent;
        this.latestChatMessageTime = latestChatMessageTime;
    }
}
