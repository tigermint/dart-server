package com.ssh.dartserver.domain.friend.presentation.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssh.dartserver.domain.university.presentation.response.UniversityResponse;
import com.ssh.dartserver.domain.user.presentation.v1.response.UserResponse;
import lombok.Data;

@Data
public class FriendResponse {
    @JsonProperty("user")
    private UserResponse userResponseDto;

    @JsonProperty("university")
    private UniversityResponse universityResponseDto;
}
