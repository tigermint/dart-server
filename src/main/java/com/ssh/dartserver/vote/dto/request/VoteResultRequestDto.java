package com.ssh.dartserver.vote.dto.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class VoteResultRequestDto {
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

