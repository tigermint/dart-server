package com.ssh.dartserver.domain.user.presentation.v1.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssh.dartserver.domain.university.presentation.response.UniversityResponse;
import lombok.Data;

@Data
public class UserWithUniversityResponse {
    @JsonProperty("user")
    private UserResponse userResponse;

    @JsonProperty("university")
    private UniversityResponse universityResponse;
}
