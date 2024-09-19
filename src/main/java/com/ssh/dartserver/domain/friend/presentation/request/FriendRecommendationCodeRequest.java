package com.ssh.dartserver.domain.friend.presentation.request;

import lombok.Data;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Data
public class FriendRecommendationCodeRequest {

    @NotNull(message = "추천인 코드는 null 일 수 없습니다.")
    @Size(min = 8, max = 8, message = "추천인 코드는 8자리입니다.")
    private String recommendationCode;

}
