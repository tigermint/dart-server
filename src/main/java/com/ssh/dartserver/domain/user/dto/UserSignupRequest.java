package com.ssh.dartserver.domain.user.dto;

import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Data
public class UserSignupRequest {
    @NotNull(message = "학교 Id는 null 일 수 없습니다.")
    private Long universityId;

    @NotNull(message = "입학년도는 null 일 수 없습니다.")
    private int admissionYear;

    @NotNull(message = "생일은 null 일 수 없습니다.")
    private int birthYear;

    @NotBlank(message = "이름은 blank 일 수 없습니다.")
    private String name;

    @NotBlank(message = "휴대폰 번호는 blank 일 수 없습니다.")
    @Pattern(regexp = "^01[016789]\\d{3,4}\\d{4}$", message = "휴대폰번호를 정확히 입력해주세요")
    private String phone;

    @NotNull(message = "성별은 null 일 수 없습니다.")
    private Gender gender;
}
