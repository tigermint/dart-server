package com.ssh.dartserver.user.controller;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserNextVoteResponseDto {
    private LocalDateTime nextVoteAvailableDateTime;
}
