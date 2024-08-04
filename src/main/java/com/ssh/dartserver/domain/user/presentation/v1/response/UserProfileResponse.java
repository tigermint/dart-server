package com.ssh.dartserver.domain.user.presentation.v1.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssh.dartserver.domain.question.application.QuestionMapper;
import com.ssh.dartserver.domain.university.presentation.response.UniversityResponse;
import com.ssh.dartserver.domain.university.application.UniversityMapper;
import com.ssh.dartserver.domain.user.domain.User;
import com.ssh.dartserver.domain.user.application.ProfileQuestionMapper;
import com.ssh.dartserver.domain.user.application.UserMapper;
import lombok.Data;

import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserProfileResponse {
    @JsonProperty("user")
    private UserResponse userResponse;

    @JsonProperty("university")
    private UniversityResponse universityResponse;

    @JsonProperty("profileQuestions")
    private List<ProfileQuestionResponse> profileQuestionResponses;

    public static UserProfileResponse of(User user) {
        final UserMapper userMapper = UserMapper.INSTANCE;
        final UniversityMapper universityMapper = UniversityMapper.INSTANCE;
        final ProfileQuestionMapper profileQuestionMapper = ProfileQuestionMapper.INSTANCE;
        final QuestionMapper questionMapper = QuestionMapper.INSTANCE;

        return userMapper.toUserProfileResponse(
                userMapper.toUserResponse(user),
                universityMapper.toUniversityResponse(user.getUniversity()),
                user.getProfileQuestions().getValues().stream()
                        .map(profileQuestion ->
                                profileQuestionMapper.toProfileQuestionResponse(
                                        questionMapper.toQuestionResponse(profileQuestion.getQuestion()),
                                        profileQuestion.getCount()
                                ))
                        .collect(Collectors.toList())
        );
    }
}
