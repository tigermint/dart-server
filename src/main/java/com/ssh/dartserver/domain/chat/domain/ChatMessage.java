package com.ssh.dartserver.domain.chat.domain;

import com.ssh.dartserver.global.common.BaseTimeEntity;
import lombok.*;

import jakarta.persistence.*;

@Entity
@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage extends BaseTimeEntity{
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "chat_message_id")
    private Long id;

    private Long senderId;

    @Enumerated(EnumType.STRING)
    private ChatMessageType chatMessageType;

    @Embedded
    private ChatContent content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private ChatRoom chatRoom;

}
