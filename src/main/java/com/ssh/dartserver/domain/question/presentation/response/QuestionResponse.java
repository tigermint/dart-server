package com.ssh.dartserver.domain.question.presentation.response;

import lombok.Data;

@Data
public class QuestionResponse {
    private Long questionId;
    private String content;
    private String icon;
}
