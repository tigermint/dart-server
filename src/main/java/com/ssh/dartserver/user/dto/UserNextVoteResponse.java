package com.ssh.dartserver.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserNextVoteResponse {
    private LocalDateTime nextVoteAvailableDateTime;
}
