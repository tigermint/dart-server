package com.ssh.dartserver.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssh.dartserver.university.dto.UniversityResponse;
import lombok.Data;

@Data
public class UserWithUniversityResponse {
    @JsonProperty("user")
    private UserResponse userResponseDto;

    @JsonProperty("university")
    private UniversityResponse universityResponseDto;
}
