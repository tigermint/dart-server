package com.ssh.dartserver.domain.chat.domain;

import com.ssh.dartserver.domain.proposal.domain.Proposal;
import com.ssh.dartserver.global.common.BaseTimeEntity;
import lombok.*;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatRoom extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_room_id")
    private Long id;

    @Column(name = "latest_chat_message_content")
    private String latestChatMessageContent;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proposal_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Proposal proposal;

    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL)
    private List<ChatRoomUser> chatRoomUsers = new ArrayList<>();

    public void updateLastMessage(String latestChatMessageContent) {
        this.latestChatMessageContent = latestChatMessageContent;
    }
}
