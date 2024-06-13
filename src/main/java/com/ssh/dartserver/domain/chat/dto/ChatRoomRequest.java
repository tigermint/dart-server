package com.ssh.dartserver.domain.chat.dto;

import lombok.Data;

public class ChatRoomRequest {
    private ChatRoomRequest() {
        throw new IllegalStateException("Utility class");
    }

    @Data
    public static class Create{
        private Long proposalId;
    }
}
