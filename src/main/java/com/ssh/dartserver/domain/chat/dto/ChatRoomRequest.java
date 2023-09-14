package com.ssh.dartserver.domain.chat.dto;

import lombok.Getter;

public class ChatRoomRequest {
    private ChatRoomRequest() {
        throw new IllegalStateException("Utility class");
    }
    @Getter
    public static class Create{
        private Long proposalId;
    }
}
