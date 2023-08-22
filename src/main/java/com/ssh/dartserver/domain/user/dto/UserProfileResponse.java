package com.ssh.dartserver.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssh.dartserver.domain.university.dto.UniversityResponse;
import lombok.Data;

import java.util.List;

@Data
public class UserProfileResponse {
    @JsonProperty("user")
    private UserResponse userResponse;

    @JsonProperty("university")
    private UniversityResponse universityResponse;

    @JsonProperty("profileQuestions")
    private List<ProfileQuestionResponse> profileQuestionResponses;
}
