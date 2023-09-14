package com.ssh.dartserver.domain.chat.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
@Getter
@NoArgsConstructor
public class ChatContent {

    @Column(name = "chat_content")
    private String value;

    private ChatContent(String value) {
        validateLength(value);
        this.value = value;
    }

    private void validateLength(String value) {
        if(value.length() > 1000) {
            throw new IllegalArgumentException("채팅 내용은 1000자를 넘을 수 없습니다.");
        }
    }

    public static ChatContent from(String value) {
        return new ChatContent(value);
    }
}
