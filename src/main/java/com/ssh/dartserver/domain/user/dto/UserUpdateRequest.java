package com.ssh.dartserver.domain.user.dto;

import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Pattern;

@Data
public class UserUpdateRequest {

    @Nullable
    @Pattern(regexp = "^$|\\S(.*\\S)?", message = "닉네임은 비어있지 않아야 합니다.")
    private String nickname;

    @Nullable
    @URL(message = "올바른 URL을 입력해주세요.")
    private String profileImageUrl;
}
