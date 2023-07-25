package com.ssh.dartserver.domain.user.dto;

import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class UserRequest {
    @NotNull(message = "학교를 선택해주세요")
    private Long universityId;

    @NotNull(message = "학번을 입력해주세요")
    private int admissionYear;

    @NotNull(message = "몇년생인지 입력해주세요")
    private int birthYear;

    @NotBlank(message = "이름을 입력해주세요")
    private String name;

    @NotBlank(message = "휴대폰번호를 입력해주세요")
    @Pattern(regexp = "^01[016789]\\d{3,4}\\d{4}$", message = "휴대폰번호를 정확히 입력해주세요")
    private String phone;

    @NotNull(message = "성별을 선택해주세요")
    private Gender gender;
}
