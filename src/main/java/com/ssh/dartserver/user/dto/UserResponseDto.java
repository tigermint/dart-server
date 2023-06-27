package com.ssh.dartserver.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDto {
    private int userId;
    private int univId;
    private String name;
    private String phone;
    private String universityName;
    private String department;
    private LocalDateTime nextVoteDateTime;
}
