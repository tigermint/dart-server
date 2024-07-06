package com.ssh.dartserver.domain.chat.domain;

import lombok.Getter;

@Getter
public enum ChatMessageType {
    ENTER("ENTER"), TALK("TALK"), QUIT("QUIT");

    private final String value;

    ChatMessageType(String value) {
        this.value = value;
    }

}
