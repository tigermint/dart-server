package com.ssh.dartserver.domain.friend.presentation.request;

import lombok.Data;

import jakarta.validation.constraints.NotNull;

@Data
public class FriendRequest {
    @NotNull(message = "친구의 id는 null 일 수 없습니다.")
    private Long friendUserId;
}
