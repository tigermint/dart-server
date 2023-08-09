package com.ssh.dartserver.domain.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserUpdateRequest {

    @NotBlank(message = "닉네임은 blank 일 수 없습니다.")
    private String nickname;

    @NotBlank(message = "프로필 이미지 URL은 blank 일 수 없습니다.")
    private String profileImageUrl;
}
