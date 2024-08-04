package com.ssh.dartserver.domain.user.presentation.v1.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssh.dartserver.domain.question.presentation.response.QuestionResponse;
import lombok.Data;

@Data
public class ProfileQuestionResponse {
    @JsonProperty("question")
    private QuestionResponse questionResponse;
    @JsonProperty("count")
    private Long count;
}
