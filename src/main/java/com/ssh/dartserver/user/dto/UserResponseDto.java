package com.ssh.dartserver.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDto {
    private Long userId;
    private Long universityId;
    private String name;
    private String phone;
    private String universityName;
    private String department;
    private LocalDateTime nextVoteDateTime;
}
