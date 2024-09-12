package com.ssh.dartserver.domain.user.presentation.v1.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserNextVoteResponse {
    private LocalDateTime nextVoteAvailableDateTime;
}
