package com.ssh.dartserver.friend.dto;

import lombok.Data;

@Data
public class FriendResponseDto {
    private Long friendUserId; //친구의 userId
    private String friendName;
    private Long universityId;
    private int admissionNum;
}
