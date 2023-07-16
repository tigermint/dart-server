package com.ssh.dartserver.friend.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class FriendRequest {
    @NotNull(message = "친구의 userId를 입력해주세요.")
    private Long friendUserId;
}
