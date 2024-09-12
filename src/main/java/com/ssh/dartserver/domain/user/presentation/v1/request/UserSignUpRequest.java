package com.ssh.dartserver.domain.user.presentation.v1.request;

import com.ssh.dartserver.domain.user.domain.personalinfo.Gender;
import com.ssh.dartserver.domain.user.domain.personalinfo.PersonalInfo;
import lombok.*;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserSignUpRequest {
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

    public PersonalInfo toPersonalInfo() {
        return PersonalInfo.of(
                this.name,
                this.phone,
                this.gender,
                this.admissionYear,
                this.birthYear
        );
    }
}
