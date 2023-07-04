package com.ssh.dartserver.question.dto;

import lombok.Data;

@Data
public class QuestionResponseDto {
    private String questionId;
    private String content;
    private String icon;
}
