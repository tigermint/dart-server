package com.ssh.dartserver.domain.user.presentation.v1.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;

@Data
public class UserStudentIdCardVerificationRequest {
    @NotBlank(message = "이름은 blank일 수 없습니다.")
    private String name;
    @NotBlank(message = "학생증 이미지 url은 blank일 수 없습니다.")
    private String studentIdCardImageUrl;
}
