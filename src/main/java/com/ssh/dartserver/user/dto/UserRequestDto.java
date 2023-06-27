package com.ssh.dartserver.user.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UserRequestDto {
    @NotBlank
    private Long universityId;

    @NotBlank(message = "학번을 입력해주세요")
    private int admissionNum;

    @NotBlank(message = "이름을 입력해주세요")
    private String name;

    @NotBlank(message = "휴대폰번호를 입력해주세요")
    @Pattern(regexp = "^01(?:0|1|[6-9])-(?:\\d{3}|\\d{4})-\\d{4}$", message = "휴대폰번호를 정확히 입력해주세요")
    private String phone;
}
