package com.ssh.dartserver.domain.vote.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class VoteResultRequest {
    @NotNull(message = "질문 id는 null 일 수 없습니다")
    private Long questionId;
    @NotNull(message = "선택된 사용자 id는 null 일 수 없습니다")
    private Long pickedUserId;
    @NotNull(message = "사용자 id는 null 일 수 없습니다")
    private Long firstUserId;
    @NotNull(message = "사용자 id는 null 일 수 없습니다")
    private Long secondUserId;
    @NotNull(message = "사용자 id는 null 일 수 없습니다")
    private Long thirdUserId;
    @NotNull(message = "사용자 id는 null 일 수 없습니다")
    private Long fourthUserId;
}

