package com.ssh.dartserver.domain.user.presentation.v1.request;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class UserUpdateRequest {

    @NotBlank(message = "닉네임은 blank 일 수 없습니다.")
    private String nickname;

    @NotBlank(message = "프로필 이미지 URL은 blank 일 수 없습니다.")
    private String profileImageUrl;

    @NotNull(message = "프로필 질문은 null 일 수 없습니다.")
    private List<Long> profileQuestionIds;
}
