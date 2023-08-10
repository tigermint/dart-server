package com.ssh.dartserver.domain.friend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssh.dartserver.domain.university.dto.UniversityResponse;
import com.ssh.dartserver.domain.user.dto.UserResponse;
import lombok.Data;

@Data
public class FriendResponse {
    @JsonProperty("user")
    private UserResponse userResponseDto;

    @JsonProperty("university")
    private UniversityResponse universityResponseDto;
}
