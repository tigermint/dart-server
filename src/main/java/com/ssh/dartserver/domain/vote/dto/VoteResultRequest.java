package com.ssh.dartserver.domain.vote.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class VoteResultRequest {
    @NotNull
    private Long questionId;
    @NotNull
    private Long pickedUserId;
    @NotNull
    private Long firstUserId;
    @NotNull
    private Long secondUserId;
    @NotNull
    private Long thirdUserId;
    @NotNull
    private Long fourthUserId;
}

