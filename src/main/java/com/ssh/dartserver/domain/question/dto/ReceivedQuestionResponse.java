package com.ssh.dartserver.domain.question.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ReceivedQuestionResponse {
    @JsonProperty("question")
    private QuestionResponse questionResponse;
    @JsonProperty("count")
    private int votedCount;
}
