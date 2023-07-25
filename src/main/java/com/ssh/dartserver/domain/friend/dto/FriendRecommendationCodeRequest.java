package com.ssh.dartserver.domain.friend.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class FriendRecommendationCodeRequest {

    @NotNull(message = "추천인 코드를 입력해주세요.")
    @Size(min = 8, max = 8, message = "추천인 코드는 8자리입니다.")
    private String recommendationCode;

}
