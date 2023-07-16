package com.ssh.dartserver.question.dto;

import lombok.Data;

@Data
public class QuestionResponse {
    private Long questionId;
    private String content;
    private String icon;
}
