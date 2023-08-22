package com.ssh.dartserver.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.ssh.dartserver.domain.question.dto.QuestionResponse;
import lombok.Data;

@Data
public class ProfileQuestionResponse {
    @JsonProperty("question")
    private QuestionResponse questionResponse;
    @JsonProperty("count")
    private Long count;
}
