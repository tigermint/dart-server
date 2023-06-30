package com.ssh.dartserver.friend.dto;

import lombok.Data;

@Data
public class RequiredFriendResponseDto {
    private Long userId;
    private Long universityId;
    private int admissionNum;
    private String name;
}
