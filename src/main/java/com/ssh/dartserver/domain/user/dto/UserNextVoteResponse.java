package com.ssh.dartserver.domain.user.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserNextVoteResponse {
    private LocalDateTime nextVoteAvailableDateTime;
}
