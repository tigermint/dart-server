package com.ssh.dartserver.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssh.dartserver.university.dto.UniversityResponseDto;
import lombok.Data;

@Data
public class UserWithUniversityResponseDto {
    @JsonProperty("user")
    private UserResponseDto userResponseDto;

    @JsonProperty("university")
    private UniversityResponseDto universityResponseDto;
}
