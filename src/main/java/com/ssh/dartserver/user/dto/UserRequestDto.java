package com.ssh.dartserver.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class UserRequestDto {
    @NotNull
    private Long universityId;

    @NotNull(message = "학번을 입력해주세요")
    private int admissionNum;

    @NotBlank(message = "이름을 입력해주세요")
    private String name;

    @NotBlank(message = "휴대폰번호를 입력해주세요")
    @Pattern(regexp = "^01[016789]-?\\d{3,4}-?\\d{4}$\n", message = "휴대폰번호를 정확히 입력해주세요")
    private String phone;
}
